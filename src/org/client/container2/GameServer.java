package org.client.container2;

import java.util.Set;
import com.google.gwt.json.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.appengine.channel.client.ChannelFactory;
import com.google.gwt.appengine.channel.client.Channel;
import com.google.gwt.appengine.channel.client.Socket;
import com.google.gwt.appengine.channel.client.SocketListener;
import com.google.gwt.appengine.channel.client.SocketError;
import com.google.gwt.appengine.channel.client.ChannelFactory.ChannelCreatedCallback;


public class GameServer {

  private final String url = "http://3-dot-smg-container-server3.appspot.com/";
  
  //for optimization
  private String matchId;
  

  private int lastMovePlayerId = -1;
  
  private JSONString accessSignature = null;
  private JSONString gameId = null;
  private JSONArray myLastMove = null;
  private JSONArray playerIds = new JSONArray();
  private GameContainer gameContainer;
  private JSONString myPlayerId = null;
  
  public GameServer() {
    ServerMessageListener.setServer(this);
  }  
  
  public void closeSocket() {
	  ServerMessageListener.closeSocket();
  }
  
  public JSONArray getPlayerIds() {
    return playerIds;
  }
  
  public String getMyPlayerId() {
    return myPlayerId.stringValue();
  }
  
  public void sendEnterQueue(
	      GameContainer gameContainer,
	      JSONString accessSignature,
	      JSONString myPlayerId,
	      JSONString gameId) {
	  
	this.gameContainer = gameContainer;
	this.accessSignature = accessSignature;
	this.gameId = gameId;
	this.myPlayerId = myPlayerId;
	
	StringBuilder sb = new StringBuilder(url);
	sb.append("queue");
	String integratedUrl = sb.toString();
	
	JSONObject postInfo = new JSONObject();
	postInfo.put("accessSignature", accessSignature);
	postInfo.put("playerId", myPlayerId);
	postInfo.put("gameId", gameId);
	String message = postInfo.toString();
	
	sendMessage(message, integratedUrl);
  }
  
  private native void sendMessage(String message, String url) /*-{
	var xmlHttp;
	if($wnd.XMLHttpRequest){
	  xmlHttp = new XMLHttpRequest();
	}else{
	  xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlHttp.onreadystatechange = function() {
	  //*****************************
	  alert(xmlHttp.readyState);
	  alert(xmlHttp.status);
	  if(xmlHttp.readyState==4 && xmlHttp.status==200) {
	    var response = xmlHttp.responseText;
	    @org.client.container2.ServerMessageListener::channelBuilder(Ljava/lang/String;) (response);
	  }
	}  
	xmlHttp.open("POST", url, true);
	xmlHttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	xmlHttp.send(message);
  }-*/;
  
  public void sendInsertNewMatch() {
    StringBuilder sb = new StringBuilder(url);
    sb.append("newMatch");
	String integratedUrl = sb.toString();
		
	JSONObject postInfo = new JSONObject();
	postInfo.put("accessSignature", this.accessSignature);
	postInfo.put("playerIds", this.playerIds);
	postInfo.put("gameId", this.gameId);
	String message = postInfo.toString();
	sendMessage1(message, integratedUrl);
  }
  
  private native void sendMessage1(String message, String url) /*-{
  var xmlHttp;
	if($wnd.XMLHttpRequest){
	  xmlHttp = new XMLHttpRequest();
	}else{
	  xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	} 
  xmlHttp.onreadystatechange = function() {
    if(xmlHttp.readyState==4 && xmlHttp.status==200) {
      var response = xmlHttp.responseText;
      @org.client.container2.ServerMessageListener::httpMessagerHandler(Ljava/lang/String;) (response);
    }
  }  
  xmlHttp.open("POST", url, true);
  xmlHttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
  xmlHttp.send(message);
}-*/;
  
  public void setMatchId(String matchId) {
    this.matchId = matchId;
  }
	  
  public GameContainer getGameContainer() {
    return gameContainer;
  }
	  
  public void setPlayerIds(JSONArray playerIds) {
    for(int i=0;i<playerIds.size();i++) {
	  this.playerIds.set(i, playerIds.get(i));
	}
  }
	
  public void sendMakeMove(JSONArray operations) {
	myLastMove = operations;
	StringBuilder sb = new StringBuilder(url);
	sb.append("matches/")
	  .append(matchId);
	String integratedUrl = sb.toString();
	JSONObject postInfo = new JSONObject();
	postInfo.put("accessSignature", this.accessSignature);
	postInfo.put("playerIds", this.playerIds);
	postInfo.put("operations", operations);
	String message = postInfo.toString();
	sendMessage3(message, integratedUrl);
  }
  
  private native void sendMessage3(String message, String url) /*-{
    var xmlHttp;
	if($wnd.XMLHttpRequest){
	  xmlHttp = new XMLHttpRequest();
	}else{
	  xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlHttp.onreadystatechange = function() {
	  if(xmlHttp.readyState==4 && xmlHttp.status==200) {
	    var response = xmlHttp.responseText;
	    @org.client.container2.ServerMessageListener::httpMessageHandler2(Ljava/lang/String;) (response);
	  }
	}  
    xmlHttp.open("POST", url, true);
    xmlHttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlHttp.send(message);
  }-*/;
  
  public JSONArray getMyLastMove() {
    return myLastMove;
  }
 
  private native void test(String message) /*-{
    $wnd.alert(message);
  }-*/;

}


class ServerMessageListener {
  
  private static GameServer gameServer = null;
  private static boolean signal = false;
  private static ChannelFactory channelFactory;
  private static Channel channel = null;
  private static Socket socket = null;
  
  private static JSONDeserialization jds = new JSONDeserialization();
  private static JSONSerialization js = new JSONSerialization();
  
  
  
  
  public static void setServer(GameServer gameServer) {
    ServerMessageListener.gameServer = gameServer;
  }
  
  public static void channelBuilder(String message) {
    JSONObject res = JSONParser.parseStrict(message).isObject();
    if(res.get("error") == null) {
      JSONString channelTokens_JSON = res.get("channelToken").isString();
      String channelTokens = channelTokens_JSON.stringValue();
      JSONValue playerIds_JSON = res.get("playerIds");
      buildChannel(channelTokens, playerIds_JSON);
    }else {
      throw new RuntimeException("ERROR!");
    }
  }
  
  public static void buildChannel(String token, final JSONValue playerIds_JSON) {
	ChannelFactory.createChannel(token, new ChannelCreatedCallback() {
		@Override
		public void onChannelCreated(final Channel result) {
			
		  socket = result.open(new SocketListener() {
			
			@Override
			public void onOpen() {
			  channel = result;
			  Window.alert("Channel Opened!");
			  if(playerIds_JSON != null) {
			    	signal = true;
			    	gameServer.setPlayerIds(playerIds_JSON.isArray());
			        gameServer.sendInsertNewMatch();
			  }
			}
			
			@Override
			public void onMessage(String message) {
			  channelMessageHandler(message);
			}
			
			@Override
			public void onError(SocketError error) {
				Window.alert("Channel error: " + error.getCode() + " : " + error.getDescription());
			}
			
			@Override
			public void onClose() {
				Window.alert("Channel closed!");
			}
			
		  });
		}
	});
  }
  
  public static void closeSocket() {
    socket.close();
  }
  
  public static void httpMessageHandler2(String message) {
    JSONObject res = JSONParser.parseStrict(message).isObject();
    if(res.get("error") == null) {
      Set<String> keys_ = res.keySet();
      JSONValue state_JSON = res.get("state");
      if(state_JSON != null) {
    	JSONObject state = state_JSON.isObject();
    	JSONArray lastMove = res.get("lastMove").isArray();
    	JSONString lastMovePlayerId = new JSONString("1");
    	gameServer.getGameContainer().updateUi(state, lastMove, lastMovePlayerId, gameServer.getPlayerIds());
      }
    }
  }
  
  public static native void displaying(String content) /*-{
	$doc.getElementById("state1").innerHTML = content;
  }-*/;
  
  public static void httpMessagerHandler(String message) {
    JSONObject res = JSONParser.parseStrict(message).isObject();
    JSONString matchId = res.get("matchId").isString();
    gameServer.setMatchId(matchId.stringValue());
  }
  
  public static void channelMessageHandler(String message) {
    JSONObject response = JSONParser.parseStrict(message).isObject();
    if(response.get("error") == null) {
      JSONValue matchId = response.get("matchId");
      JSONValue playerIds = response.get("playerIds");
      if(matchId != null && playerIds != null) {
        gameServer.setMatchId((matchId.isString().stringValue())); 
        gameServer.setPlayerIds(playerIds.isArray());
        gameServer.getGameContainer().updateUi(new JSONObject(), new JSONArray(), new JSONString("1"), gameServer.getPlayerIds());
      }else {
    	JSONValue state_JSON = response.get("state");
    	if(state_JSON!=null) {
    	  JSONObject state = state_JSON.isObject();
    	  JSONArray lastMove = response.get("lastMove").isArray();
    	  JSONString lastMovePlayerId = new JSONString("1");
    	  gameServer.getGameContainer().updateUi(state, lastMove, lastMovePlayerId, gameServer.getPlayerIds());
    	}
      }
    }else {
      throw new RuntimeException("ERROR!");
    }
  }

  private static native void test(String message) /*-{
    $wnd.alert(message);
  }-*/;
}
