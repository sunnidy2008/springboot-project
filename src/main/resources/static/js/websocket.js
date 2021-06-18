var __websocket; 
var __vm=null;

function connnectToWebwebsocket(userid,vm){
	if(typeof(WebSocket) == "undefined") {  
	    console.log("您的浏览器不支持WebSocket");  
	}else{  
		__vm = vm;
	    console.log("您的浏览器支持WebSocket");  
		//实现化WebSocket对象，指定要连接的服务器地址与端口  建立连接  
	    //等同于__websocket = new WebSocket("ws://localhost:8083/checkcentersys/web__websocket/20");  
	    //__websocket = new WebSocket("${basePath}web__websocket/${cid}".replace("http","ws"));  
	    
	    __websocket = new WebSocket("ws://"+__SERVER_IP+":"+__SERVER_PORT+"/websocket/"+userid); 
	    //打开事件  
	    __websocket.onopen = function() {  
	        console.log("Socket 已打开");  
	        //__websocket.send("这是来自客户端的消息" + location.href + new Date());  
	    };  
	    //获得消息事件  
	    __websocket.onmessage = function(msg) {  
	        console.log(msg.data);  
	        __onmessage(msg.data);
	        //发现消息进入    开始处理前端触发逻辑
	    };  
	    //关闭事件  
	    __websocket.onclose = function() {  
	        console.log("Socket已关闭");  
	    };  
	    //发生了错误事件  
	    __websocket.onerror = function() {  
	        alert("Socket发生了错误");  
	        //此时可以尝试刷新页面
	    }  
	    //离开页面时，关闭__websocket
	    //jquery1.8中已经被废弃，3.0中已经移除
	    // $(window).unload(function(){  
	    //     __websocket.close();  
	    //});  
	}
}

var __Notifications = {};
function __onmessage(data){
	__vm.unreadNum = __vm.unreadNum+1;
	var jsonData = JSON.parse(data);
	var notification = __vm.$notify({
         title: '提示',
         message: jsonData.cText,
         duration: 0,
         onClose:function(){debugger;
        	//setMessageReaded();
        	console.log("onClose");
        	utils.post("/api/sysWsMessage/read", {id:jsonData.cPkId}).then(function (res) {
    	    });
        	delete __Notifications[jsonData.cPkId];
        	__vm.unreadNum = __vm.unreadNum-1;
        	if(__vm.unreadNum <0){
        		__vm.unreadNum = 0;
        	}
         },
         onClick:function(){
        	utils.openPopWin("消息",jsonData.cUrl);
        	__Notifications[jsonData.cPkId].close();
        	delete __Notifications[jsonData.cPkId];
         }
       });
	__Notifications[jsonData.cPkId] = notification;
}

