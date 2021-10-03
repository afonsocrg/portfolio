package pt.tecnico.hds.mad.client;

import pt.tecnico.hds.mad.client.exceptions.*;
import pt.tecnico.hds.mad.lib.contract.Record;
import pt.tecnico.hds.mad.lib.exceptions.KeyPoolException;

import java.net.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;

public class LocationProofManager implements Runnable {
    private int port;

    private User user;
    // most used attributes from user
    private String id;
    private Grid grid;

    private LocalTime baseTime;
    private AtomicInteger currEpoch = new AtomicInteger(-1);

    public User getUser() { return this.user; }

    public LocationProofManager(User user, int port) {
        this.port = port;
        this.user = user;
        this.id = user.getId();
        this.grid = user.getGrid();

        this.baseTime = LocalTime.now();
    }

    private int nextEpoch() {
        return this.currEpoch.incrementAndGet();
    }

    public LocalTime getBaseTime() { return this.baseTime; }
    public void incrementBaseTime(int delta, ChronoUnit chronoUnit) {
        this.baseTime = this.baseTime.plus(delta, chronoUnit);
    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        try {
            Random rnd = new Random();
            int delta = 30;

            while (true) {
                // Sleep until epoch
                if (LocalTime.now().isBefore(getBaseTime())) {
                    TimeUnit.SECONDS.sleep(Duration.between(LocalTime.now(), getBaseTime()).toSeconds());
                }
                int epoch = nextEpoch();

                // sleep a random time to avoid flooding the network
                TimeUnit.SECONDS.sleep(rnd.nextInt(delta/3));

                incrementBaseTime(delta, ChronoUnit.SECONDS);

                try {
                    LocationProofRequester requester = new LocationProofRequester(this, this.getRecord(epoch));
                    (new Thread(requester)).start();
                } catch (SocketException | GeneralSecurityException e) {
                    System.err.println("[-] Failed to create location proof requester. skipping epoch...");
                    continue;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvalidEpochException e) {
            // end of epoch. exiting
            System.out.println("Reached end of epochs. Finishing requesting service...");
            return;
        }
    }

    Record getRecord(int epoch) throws InvalidEpochException {
        Position myPos;
        try {
            myPos = this.grid.getUserPosition(epoch, id);
        } catch (NoSuchUserException e) {
            System.err.printf("Error: Could not find myself in grid at epoch %d\n", epoch);
            System.exit(-1);
            // will not run
            return null;
        }
        return new Record(id, epoch, myPos.getX(), myPos.getY());
    }
}
