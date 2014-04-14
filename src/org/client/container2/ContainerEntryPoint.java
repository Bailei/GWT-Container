package org.client.container2;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.RootPanel;

public class ContainerEntryPoint implements EntryPoint {
	private static GameContainer gameContainer = null;
	
	// For the countdown
	private Label countdown = new Label();
	
	@Override
	public void onModuleLoad() {
	  setListener();
	  gameContainer = new GameContainer(countdown);
	  
	  Button close = new Button("close socket");
	  
	  close.addClickHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
	      gameContainer.closeSocket();
	    }
	  });
	  
	  RootPanel.get("mainDiv").add(countdown);
	  RootPanel.get("mainDiv").add(close);
	}
	
	public static void messageListener(String message) {
	  JSONObject res = JSONParser.parseStrict(message).isObject();
	  if(res != null) {
	    String request = res.get("request").isString().stringValue();
	    switch (request) {
	      case "SEND_ENTER_QUEUE": {
	        gameContainer.sendEnterQueue(res.get("gameId").isString());
	        break;
	      }
	      case "SEND_MAKE_MOVE": {
	    	gameContainer.sendMakeMove(res.get("move").isArray());
	        break;
	      }
	      default: {
	        throw new RuntimeException("No such Request");
	      }
	    }
	  }else {
	    throw new RuntimeException("JSON Parse Error!");
	  }
	}
	
	public native void setListener() /*-{
	  $wnd.addEventListener("message", function(event) {
	  	@org.client.container2.ContainerEntryPoint::messageListener(Ljava/lang/String;) (event.data);
	  },
	  false);
	}-*/;

	private static native void test(String message) /*-{
	  alert(message);
	}-*/;
}
