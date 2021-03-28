package platform;

import java.util.UUID;

public class RandomStringUUID {
    public static String getRandomUUIDString() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }


}
