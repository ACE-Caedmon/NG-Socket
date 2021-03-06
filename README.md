NG-Socket
=========

基于Netty框架开发的网络游戏服务器通信框架,API更加简单直观,提供系统模块化,事件驱动模型,网络协议安全等功能。
一.功能支持
-----------------------------------
目前支持TCP协议,WebSocket协议。同时支持Server端和Client端

二.使用方法
-----------------------------------
        Server端
        PropertyConfigurator.configure("conf/log4j.properties");
        ServerSettings settings= new ServerSettings();
        settings.protocol= SocketEngine.WEBSOCKET_PROTOCOL;
        settings.port=8001;
        ServerSocketEngine engine=new ServerSocketEngine(settings);
        engine.registerExtension(new TestExtension());
        engine.start();
        Client端
        PropertyConfigurator.configure("conf/log4j.properties");
        WsClientSettings settings=new WsClientSettings();
        settings.url="ws://localhost:8001/websocket";
        CmdFactoryCenter cmdFactoryCenter=new DefaultCmdFactoryCenter(2);
        WsClientSocketEngine socketEngine=new WsClientSocketEngine(settings,cmdFactoryCenter);
        socketEngine.registerExtension(new TestExtension());
        socketEngine.start();

-----------------------------------
三.网络协议
-----------------------------------  
协议格式详细可以查看doc目录下对应的com.ace.ng.codec.encrypt.ServerBinaryEncryptDecoder类的注释。
com.ace.ng.codec.encrypt.ServerBinaryEncryptEncoder
四.消息解码
-----------------------------------
### 性能优化
大多网络消息层的处理为了面向对象，会使用反射技术。但是，反射技术相对来说性能会比较低,在大规模并发环境下解码性能也是很关键的一步。
NG-Socket的解码层采用Javassit动态更改字节码，并且使用缓存等方法大大优化解码性能。

### 面向对象
我们如果要写一个消息处理器会先实现继承SessionCmdHandler接口
CmdHandler解码有两种方式。

1.重写decode方法,然后手动对每个属性赋值,在CustomBuf中封装了各种常见的解码接口，方便开发者使用。

        @Cmd(id=1,desc="测试用")
        public class Handler0001 extends SessionCmdHandler {
                private String content;
                @Override
                public void decode(CustomBuf buf){
                this.content=buf.readString();
                }
                @Override
                public void execute(ISession playerOnline) {
                        System.out.println("Server recived:"+content);
                        Message001 message001=new Message001();
                        message001.setContent("content");
                        playerOnline.send((short)1,message001);
                }
        }
2.使用系统提供的自动解码

    @Cmd(id=1,desc="测试用")
    public class Handler0001 extends SessionCmdHandler {
        private String content;
        @NotDecode
        private int id;
        public void setContent(String content) {
            this.content = content;
        }
        @Override
        public void execute(ISession playerOnline) {
            System.out.println("Server recived:"+content);
            Message001 message001=new Message001();
            message001.setContent("content");
            playerOnline.send((short)1,message001);
        }
    }
在上述代码中，不用重写decode,只需要对需要解码赋值的属性提供set方法,框架就会自动帮你解码。不需要解码的属性,加一个@NotDecode的标记就可以了。
前提是定义类属性的顺序要跟消息包的顺序一致。

六.线程模型
-----------------------------------
业务线程模型与Netty框架层的线程分离，互不干扰,依赖于我的另一个项目Game-Current。在本项目中,确保业务逻辑线程池不会对网络通信数据读取以及解码造成影响，提高并发度。
Netty本身是每个Socket连接对应一个线程，同一个客户端的请求数据更改就不会出现线程安全方面的问题。Game-Current依然支持此模型，只是更进一层，你可以针对业务进行扩展，比如副本，每个副本中分配一个IActor,
在需要考虑线程安全的问题，统一使用IActor进行任务调度。详细见Game-Current相关文档。

七.模块扩展
-----------------------------------
想要新增一个系统模块非常简单,继承Extension类就可以了，然后在init方法中注册相关消息处理器。

八.自定义事件
-----------------------------------
NG-Socket帮你设计定义事件触发模型,并且提供泛型支持，让你随心所欲的自定义事件模型,详细查看com.ace.ng.event包中的文档。

简单实例demo在com.ace.ng.examples包中。
