﻿using System;
using System.Collections.Generic;
using System.Text;

namespace Taobao.Top.Link.Endpoints
{
    public class MessageType
    {
        public const short CONNECT = 0;
        public const short CONNECTACK = 1;
        public const short SEND = 2;
        public const short SENDACK = 3;

        public class HeaderType
        {
            public const short EndOfHeaders = 0;
            public const short Custom = 1;
            public const short StatusCode = 2;
            public const short StatusPhrase = 3;
            public const short Flag = 4;
            public const short Token = 5;
        }
    }
}
