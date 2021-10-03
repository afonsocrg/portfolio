package pt.tecnico.hds.mad.lib;

public class Utils {

    private static final int SERVER_BASE_PORT = 8080;
    private static final int CLIENT_BASE_PORT = 7090;

    public static int extractIdNumberFromString(String idString) {
        return Integer.parseInt(idString.replaceAll("[^0-9]", ""));
    }

    public static int getServerPortFromId(String idString) {
        return SERVER_BASE_PORT + 2 * extractIdNumberFromString(idString);
    }

    public static int getLocationRequesterPortFromId(String idString) {
        return CLIENT_BASE_PORT + 2 * extractIdNumberFromString(idString);
    }

    public static int getListenerServicePortFromId(String idString) {
        return CLIENT_BASE_PORT + 2 * extractIdNumberFromString(idString) + 1;
    }

    public static int getByzantineQuorum() {
        return (int) Math.ceil((HelperConstants.NUM_SERVERS + HelperConstants.MAX_BYZANTINE_SERVERS) / 2.0);
    }

    public static int getRequiredProofsCount() {
        return HelperConstants.MAX_BYZANTINE_USERS + 1;
    }

}
