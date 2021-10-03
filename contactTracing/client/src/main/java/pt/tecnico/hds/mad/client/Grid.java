package pt.tecnico.hds.mad.client;

import pt.tecnico.hds.mad.client.exceptions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Grid {
    private final Map<Integer, Map<String, GridUser>> usersByEpoch = new HashMap<>();

    public Grid(String gridConfigFile) throws UserException {
        try (Scanner fileScanner = new Scanner(new File(gridConfigFile))) {
            // first line has grid_x and grid_y
            String line = fileScanner.nextLine();
            String[] split = line.split(",");
            int maxX = Integer.parseInt(split[0]);
            int maxY = Integer.parseInt(split[1]);

            while (fileScanner.hasNext()) {
                line = fileScanner.nextLine();
                split = line.split(",");

                String id = split[0];
                int epoch = Integer.parseInt(split[1]);
                int userX = Integer.parseInt(split[2]);
                int userY = Integer.parseInt(split[3]);

                if (userX > maxX || userY > maxY) {
                    throw new UserException(ErrorMessages.BAD_DIMENSIONS);
                }
                addUserToGrid(id, epoch, userX, userY);
            }
        } catch (FileNotFoundException | UserException e) {
            throw new UserException(e.getMessage());
        }
    }

    private void addUserToGrid(String id, int epoch, int userX, int userY) {

        this.usersByEpoch.putIfAbsent(epoch, new HashMap<>());
        this.usersByEpoch.get(epoch).put(id, new GridUser(id, userX, userY));
    }



    private Map<String, GridUser> getUsersAtEpoch(int epoch) throws InvalidEpochException {
        if(this.usersByEpoch.containsKey(epoch)) {
            return this.usersByEpoch.get(epoch);
        }
        throw new InvalidEpochException();
    }

    public GridUser getUser(int epoch, String id) throws InvalidEpochException, NoSuchUserException {
        Map<String, GridUser> usersAtEpoch = getUsersAtEpoch(epoch);

        if(usersAtEpoch.containsKey(id)) {
            return usersAtEpoch.get(id);
        }
        throw new NoSuchUserException();
    }

    public Position getUserPosition(int epoch, String userId) throws InvalidEpochException, NoSuchUserException {
        return getUser(epoch, userId).getPos();
    }


    public boolean isNearAtEpoch(int epoch, String userA, String userB) throws
        InvalidEpochException, NoSuchUserException
    {
        Position aPos = getUserPosition(epoch, userA);
        Position bPos = getUserPosition(epoch, userB);
        return aPos.isNear(bPos);
    }

    public List<GridUser> whoIsNearAtEpoch(int epoch, String targetId) throws InvalidEpochException {
        List<GridUser> list = new ArrayList<>();
        for (Map.Entry<String, GridUser> entry: getUsersAtEpoch(epoch).entrySet()) {
            String otherId = entry.getKey();
            GridUser otherUser = entry.getValue();
            try {
                if (!otherId.equals(targetId) && isNearAtEpoch(epoch, targetId, otherId)) {
                    list.add(otherUser);
                }
            } catch (NoSuchUserException e) {
                // we know it won't happen
                System.err.println("Existing user doesn't exist...");
                e.printStackTrace();
                System.exit(-1);
            }
        }
        return list;
    }
}
