using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading;

namespace Lab4Try2
{
    
    
    public class Callbacks
    {
        
        public static void ReceiveCallback(IAsyncResult ar)
        {
            StateObject so = (StateObject) ar.AsyncState;
            Socket s = so.workSocket;

            int read = s.EndReceive(ar);
            if (read > 0) {
                if (so.parseHeader)
                {
                    if (so.header.Contains("\r\n\r\n"))
                    {
                        // header is received, parsing content length
                        Regex reg = new Regex("\\\r\nContent-Length: (.*?)\\\r\n");
                        Match m = reg.Match(so.header);
                        so.contentLength = int.Parse(m.Groups[1].ToString());
                        // read the body
                        so.buffer = new byte[so.contentLength];
                        so.parseHeader = false;
                        s.BeginReceive(so.buffer, 0, so.contentLength, 0, ReceiveCallback, so);
                    }
                    else
                    {
                        // read the header byte by byte, until \r\n\r\n
                        so.header += Encoding.ASCII.GetString(so.buffer);
                        s.BeginReceive(so.buffer, 0, 1, 0, new AsyncCallback(ReceiveCallback), so);
                    }
                }
                else
                {
                    // read the body
                    so.sb.Append(Encoding.ASCII.GetString(so.buffer, 0, read));
                    s.BeginReceive(so.buffer, 0, so.contentLength, 0, new AsyncCallback(ReceiveCallback), so);
                }
            }
            else{
                if (so.sb.Length > 1)
                {
                    so.are.Set();
                }
                s.Close();
            }

        }
        public static void SendCallback(IAsyncResult ar)
        {
            StateObject so = (StateObject) ar.AsyncState;
            so.workSocket.EndSend(ar);
            so.buffer = new byte[1];
            so.parseHeader = true;
            IAsyncResult receiveResult = so.workSocket.BeginReceive(
                so.buffer, 0, 1 ,0, new AsyncCallback(ReceiveCallback), so);
            
        }
        
        public static void ConnectCallback(IAsyncResult ar)
        {
            StateObject so = (StateObject) ar.AsyncState;
            Socket s = so.workSocket;
            s.EndSend(ar);
            so.request = "GET " + so.resource +" HTTP/1.0\r\n" +
                         "Host: " + so.server + "\r\n" +
                         "Connection: Close\r\n" +
                         "Accept: text/html\r\n" +
                         "User-Agent: CSharpTests\r\n\r\n";
            
            Byte[] bytesSent = Encoding.ASCII.GetBytes(so.request);
            s.BeginSend(
                bytesSent, 0, bytesSent.Length, 0, new AsyncCallback(SendCallback), so);
        }

        
        
        public static void Run(Host[] hosts)
        {
            int port = 80;
            List<StateObject> sos = new List<StateObject>();
            List<AutoResetEvent> handles = new List<AutoResetEvent>();
            foreach (Host host in hosts)
            {
                Socket s = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                StateObject so = new StateObject();
                handles.Add(so.are);
                sos.Add(so);
                so.port = port;
                so.server = host.address;
                so.resource = host.resource;
                so.workSocket = s;
                s.BeginConnect(Dns.GetHostAddresses(host.address)[0], port, new AsyncCallback(ConnectCallback), so);
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
    }
}