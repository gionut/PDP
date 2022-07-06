using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading;

namespace Lab4Try2
{
    public class StateObject
    {
        public string server = "";
        public string resource = "";
        public int port = 80;
        public string request = "";
        public Socket workSocket = null;
        public byte[] buffer = null;
        public StringBuilder sb = new StringBuilder();
        public AutoResetEvent are = new AutoResetEvent(false);
        public string header = "";
        public bool parseHeader = false;
        public int contentLength = 0;
    }
    
    public class Host
    {
        public string address;
        public string resource;

        public Host(string address, string resource)
        {
            this.address = address;
            this.resource = resource;
        }
    }
    
    public class MainProgram
    {
        public static void Main(string[] args)
        {
            Host[] hosts = new Host[]
            {
                new Host("www.cs.ubbcluj.ro", "/~rlupsa/edu/pdp/progs/srv-begin-end.cs"),
                new Host("www.cs.ubbcluj.ro", "/~rlupsa/edu/pdp/progs/srv-task.cs"),
                new Host("www.cs.ubbcluj.ro", "/~rlupsa/edu/pdp/progs/srv-await.cs"),
                new Host("www.cs.ubbcluj.ro", "/~rlupsa/edu/pdp/lab-4-futures-continuations.html"),
                new Host("www.cs.ubbcluj.ro", "/~rlupsa/edu/pdp/")
            };
            int port = 80;
           
            //Callbacks.Run(hosts);
            Tasks.Run(hosts);
            // Await.Run(hosts);
        }
        
    }
}