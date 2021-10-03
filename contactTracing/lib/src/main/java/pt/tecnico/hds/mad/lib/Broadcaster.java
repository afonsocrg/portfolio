package pt.tecnico.hds.mad.lib;

import com.google.gson.*;

/*
 * Must be implemented by entities
 * that use broadcast primitives
 */
public interface Broadcaster {
    public void deliverMessage(JsonObject message);
    public String getId();
}
