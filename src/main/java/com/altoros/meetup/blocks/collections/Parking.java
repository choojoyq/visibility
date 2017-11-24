package com.altoros.meetup.blocks.collections;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @author Nikita Gorbachevski
 */
public class Parking {

  // a non blocking queue can be used instead of blocking
  private final BlockingQueue<Integer> queue;
  private final CopyOnWriteArrayList<ParkingPlace> places;

  public Parking(int numberOfPlaces) {
    places = new CopyOnWriteArrayList<>();
    queue = new PriorityBlockingQueue<>(numberOfPlaces, Integer::compareTo);
    for (int i = 0; i < numberOfPlaces; i++) {
      // preallocate
      places.add(null);
      queue.add(i);
    }
  }

  public static class ParkingPlace {
    private final long timeout;
    private final long id;

    public ParkingPlace(long timeout, long id) {
      this.timeout = timeout;
      this.id = id;
    }

    public long getTimeout() {
      return timeout;
    }

    public long getId() {
      return id;
    }

    @Override
    public String toString() {
      return "ParkingPlace{" +
          "timeout=" + timeout +
          ", id=" + id +
          '}';
    }
  }

  public static void main(String[] args) throws Exception {
    final Parking parking = new Parking(5);
    int numOfCars = 12;
    ExecutorService executorService = Executors.newFixedThreadPool(numOfCars);
    for (int i = 0; i < numOfCars; i++) {
      executorService.submit(parking::park);
    }
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      // ignored
    }
    System.out.println(parking.getState());
    executorService.shutdown();
    executorService.awaitTermination(5, TimeUnit.SECONDS);
  }

  public void park() {
    long id = Thread.currentThread().getId();
    Integer place;
    try {
      // should be replaced with busy loop for non-blocking queue
      System.out.println("Car with id " + id + " has come to the parking");
      place = queue.take();
    } catch (InterruptedException e) {
      // ignore
      return;
    }
    long timeout = (new Random().nextInt(10) + 1) * 100L;
    System.out.println("Car with id " + id + " will park for " + timeout + " millis at place " + place);
    ParkingPlace parkingPlace = new ParkingPlace(timeout, id);
    try {
      places.set(place, parkingPlace);
      try {
        Thread.sleep(timeout);
      } catch (InterruptedException e) {
        // ignore
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      System.out.println("Car with id " + id + " has left the parking place " + place);
      // free place and return it back to queue
      places.set(place, null);
      queue.add(place);
    }
  }

  public List<ParkingPlace> getState() {
    return places;
  }
}
