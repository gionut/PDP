using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading;
using System.Threading.Tasks;

namespace Lab4Try2
{
    public class Tasks
    {

        public static void Run(Host[] hosts)
        {
            int port = 80;
            List<StateObject> sos = new List<StateObject>();
            List<AutoResetEvent> handles = new List<AutoResetEvent>();
            List<Task> tasks = new List<Task>();
            foreach (Host host in hosts)
            {
                Socket s = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                StateObject so = new StateObject();
                handles.Add(so.are);
                so.port = port;
                so.server = host.address;
                so.resource = host.resource;
                so.workSocket = s;
                sos.Add(so);
                Task t = Task.Run(() => Connect(so));
            }
        
            foreach (var handle in handles)
            {
                handle.WaitOne();
            }
           
            foreach (var so in sos)
            {
                Console.WriteLine(so.sb.ToString());
            }
                
        }
        
        static void ReceiveResponse(Task<StateObject> t)
        {
            StateObject so = t.Result;
            Socket s = so.workSocket;
            
            Task<StateObject> future = new Task<StateObject>(() =>
            {
                // if enter from SendRequest, the length of so.buffer and contentLength should be 1, for parsing the header
                // if enter from ReceiveResponse, which previously entered on "header is received" branch, both properties will be the length of the body in the response
                s.Receive(so.buffer, 0, so.contentLength, 0);
                return so;
            });
            future.Start();
            future.ContinueWith((Task<StateObject> completedTask) =>
            {
                int read = completedTask.Result.buffer.Length;
        
                if (so.parseHeader)
                {
                    if (so.header.Contains("\r\n\r\n"))
                    {
                        // header is received, parsing content length
                        // I use regular expressions, but any other method you can think of is ok
                        Regex reg = new Regex("\\\r\nContent-Length: (.*?)\\\r\n");
                        Match m = reg.Match(so.header);
                        so.contentLength = int.Parse(m.Groups[1].ToString());
                        // read the body
                        so.buffer = new byte[so.contentLength];
                        so.parseHeader = false;
                    }
                    else
                    {
                        // read the header byte by byte, until \r\n\r\n
                        so.header += Encoding.ASCII.GetString(so.buffer);
                    }
                }
                else
                {
                    // read the body
                    so.sb.Append(Encoding.ASCII.GetString(so.buffer, 0, read));
                    so.are.Set();
                    s.Close();
                }
                if (s.Connected)
                    ReceiveResponse(completedTask);
            });
        }
        
        static void SendRequest(Task<StateObject> t)
        {
            StateObject so = t.Result;
            Socket s = so.workSocket;
            Task<StateObject> future = new Task<StateObject>(() =>
            {
                so.request = "GET " + so.resource +" HTTP/1.0\r\n" +
                             "Host: " + so.server + "\r\n" +
                             "Connection: Close\r\n" +
                             "Accept: text/html\r\n" +
                             "User-Agent: CSharpTests\r\n\r\n";
                Byte[] bytesSent = Encoding.ASCII.GetBytes(so.request);
                s.Send(bytesSent, 0, bytesSent.Length, 0);
                return so;
            });
            future.Start();
            future.ContinueWith((Task<StateObject> completedTask) =>
            {
                so.buffer = new byte[1];
                so.contentLength = 1;
                so.parseHeader = true;
                ReceiveResponse(completedTask);
            });
        }
        
        static void Connect(StateObject so)
        {
            Socket s = so.workSocket;
            Task<StateObject> future = new Task<StateObject>(() =>
            {
                s.Connect(Dns.GetHostAddresses(so.server)[0], so.port);
                return so;
            });
            future.Start();
            future.ContinueWith((Task<StateObject> completedTask) => SendRequest(completedTask));
        }
    }
}