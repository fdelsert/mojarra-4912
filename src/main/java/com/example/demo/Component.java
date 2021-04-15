package com.example.demo;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.*;
import java.io.IOException;
import java.util.Map;

@FacesComponent("test")
@ListenersFor({@ListenerFor(systemEventClass = PostAddToViewEvent.class), @ListenerFor(systemEventClass = PreRenderViewEvent.class)})
public class Component extends UINamingContainer implements SystemEventListener {

    public void processEvent(SystemEvent systemEvent) throws AbortProcessingException {
        if (systemEvent instanceof ComponentSystemEvent) {
            processEvent((ComponentSystemEvent) systemEvent);
        }
    }

    @Override
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        if (event instanceof PostAddToViewEvent) {
            UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
            root.subscribeToViewEvent(PreRenderViewEvent.class, this);
        }
        if (event.getSource() instanceof UIViewRoot
                && event instanceof PreRenderViewEvent) {
            createChildrenComponents(FacesContext.getCurrentInstance());
        }
    }

    void createChildrenComponents(FacesContext context) {
        for (int i = 0; i < 3; i++) {
            includeCompositeComponent(this, "http://xmlns.jcp.org/jsf/composite/test", "simple", "child-" + i, null);
        }
    }

    public static UIComponent includeCompositeComponent(UIComponent parent, String taglibURI, String tagName, String id, Map<String, Object> param) {
        FacesContext context = FacesContext.getCurrentInstance();
        UIComponent composite = context.getApplication().getViewHandler()
                .getViewDeclarationLanguage(context, context.getViewRoot().getViewId())
                .createComponent(context, taglibURI, tagName, param);
        composite.setId(id);
        parent.getChildren().add(composite);
        return composite;
    }

    @Override
    public boolean isListenerForSource(Object o) {
        return (o instanceof UIViewRoot) || (o == this);
    }

    @Override
    public boolean isTransient() {
        return true;
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.startElement("div", null);
        responseWriter.writeAttribute("id", getClientId(context), null);
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.endElement("div");
    }

    @Override
    public boolean getRendersChildren() {
        return false;
    }
}
