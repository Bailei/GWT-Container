package org.client.container;

import com.google.gwt.core.client.EntryPoint;

import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class ContainerEntryPoint implements EntryPoint {
	Container container = null;
	
	@Override
	public void onModuleLoad() {
	  //GameControllerMessageListener listener = new GameControllerMessageListener();
	  //listener.setListener(); 
	
	  container = new Container();
	  
      Button refresh = new Button("refresh");
      refresh.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          container.updateGame();
        }
      });
      
      FlowPanel flowPanel = new FlowPanel();
     
      flowPanel.add(refresh);
      
      RootPanel.get("mainDiv").add(flowPanel);     
	}
	
	public class GameControllerMessageListener {
		
	  private void receivedMessage(String jsonStringMessage) {
		  RootPanel.get("mainDiv").add(new Button("Got it"));
	      container.sendGameReady();
	  }
 	
	  public native void setListener() /*-{	  	
        window.addEventListener(
           "message", 
           function(e) {
             var val = e.data;
             this.@org.client.container.ContainerEntryPoint.GameControllerMessageListener::receivedMessage(Ljava/lang/String;) (JSON.stringify(val));
           },
           false);  
	  }-*/;
	  
	}
}
