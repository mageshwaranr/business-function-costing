package com.tt.xenon.common.test;

import org.junit.Test;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static java.util.Arrays.asList;

/**
 * Created by mageshwaranr on 8/31/2016.
 */
public class CompletableFutureTest {

  @Test
  public void testMain() throws Exception {
    CompletableFuture<String> contentsCF = readPage();

    CompletableFuture<List<String>> linksCF =
        contentsCF.thenApply(CompletableFutureTest::getLinks);

    CompletableFuture<Void> completionStage = linksCF.thenAccept(list ->
    {
      String a = null;
      System.out.println(a.toString());
    });

    // This will NOT cause an exception to be thrown, because
    // the part that was passed to "thenAccept" will NOT be
    // evaluated (it will be executed, but the exception will
    // not show up)
    List<String> result = linksCF.get();
    System.out.println("Got " + result);


    // This will cause the exception to be thrown and
    // wrapped into an ExecutionException. The cause
    // of this ExecutionException can be obtained:
    try {
      completionStage.get();
    } catch (ExecutionException e) {
      System.out.println("Cought " + e);
      Throwable cause = e.getCause();
      System.out.println("cause: " + cause);
    }

    // Alternatively, the exception may be handled by
    // the future directly:
    CompletableFuture<Void> exceptionally = completionStage.exceptionally(e ->
    {
      System.out.println("Future exceptionally finished: " + e);
      return null;
    });

    try {
      completionStage.get();
    } catch (Throwable t) {
      System.out.println("Already handled by the future " + t);
    }
  }


  @Test
  public void testComposable() throws Exception {

    CompletableFuture<List<String>> one = findByName("One");
    CompletableFuture<List<String>> two = findByName("Two");
    CompletableFuture<List<String>> five = findByName("Five");
    CompletableFuture<List<String>> three = findByName("Three");

    CompletableFuture<AbstractMap.SimpleEntry<String, String>> futureOneTwo = one.thenCombine(two, (strings, strings2) -> {
      System.out.println("CompletableFutureTest.testComposable.futureOneTwo.one.thenCombine(two)");
      return new AbstractMap.SimpleEntry<>(strings.get(0), strings.get(1));
    });

    CompletableFuture<AbstractMap.SimpleEntry<String, String>> futureThreeFive = three.thenCombine(five, (strings, strings2) -> {
      System.out.println("CompletableFutureTest.testComposable.futureThreeFive.three.thenCombine(five)");
      return new AbstractMap.SimpleEntry<>(strings.get(0), strings.get(1));
    });

    CompletableFuture<AbstractMap.SimpleEntry<String, String>> futureFiveTHree = five.thenCombine(three, (strings, strings2) -> {
      System.out.println("CompletableFutureTest.testComposable.futureThreeFive.five.thenCombine(three,)");
      return new AbstractMap.SimpleEntry<>(strings.get(0), strings.get(1));
    });

    CompletableFuture<Map<String, String>> mapOne = futureOneTwo.thenComposeAsync(listListSimpleEntry -> {
      System.out.println("futureOneTwo.thenComposeAsync(listListSimpleEntry) key");
      return updateInfo(10, listListSimpleEntry.getKey());
    });
    CompletableFuture<Map<String, String>> mapTwo = futureOneTwo.thenComposeAsync(listListSimpleEntry -> {
      System.out.println("futureOneTwo.thenComposeAsync(listListSimpleEntry) value");
      return updateInfo(-10, listListSimpleEntry.getValue());
    });
    CompletableFuture<Map<String, String>> mapThree = futureThreeFive
        .thenComposeAsync(listListSimpleEntry -> updateInfo(10, listListSimpleEntry.getKey()))
        .exceptionally(throwable -> {
          System.out.println("futureThreeFive.thenComposeAsync.exceptionally.five.thenCombine(three,)");
          return null;
        });
    CompletableFuture<Map<String, String>> mapFive = futureThreeFive.thenComposeAsync(listListSimpleEntry -> {
      System.out.println("futureThreeFive.thenComposeAsync(listListSimpleEntry) value");
      return updateInfo(-10, listListSimpleEntry.getValue());
    });
    CompletableFuture<Map<String, String>> mapThree53 = futureFiveTHree.thenComposeAsync(listListSimpleEntry -> {
      System.out.println("futureFiveTHree.thenComposeAsync(listListSimpleEntry) key");
      return updateInfo(10, listListSimpleEntry.getKey());
    });
    CompletableFuture<Map<String, String>> mapFive53 = futureFiveTHree.thenComposeAsync(listListSimpleEntry -> {
      System.out.println("futureFiveTHree.thenComposeAsync(listListSimpleEntry) value");
      return updateInfo(-10, listListSimpleEntry.getValue());
    });

    mapFive53.exceptionally(throwable -> {
      System.out.println("mapFive53.exceptionally(throwable) ");
//      throwable.printStackTrace();
      return null;
    });
    mapThree53.exceptionally(throwable -> {
      System.out.println("mapThree53.exceptionally(throwable) ");
//      throwable.printStackTrace();
      return null;
    });

    try {
      mapOne.get();
    } catch (Exception e) {
    }
    try {
      mapTwo.get();
    } catch (Exception e) {
    }

    try {
      mapThree.get();
    } catch (Exception e) {
    }

    try {
      mapFive.get();
    } catch (Exception e) {
    }

    try {
      mapThree53.get();
    } catch (Exception e) {
    }
    try {
      mapFive53.get();
    } catch (Exception e) {
    }

  }

  private static List<String> getLinks(String s) {
    System.out.println("Getting links...");
    List<String> links = new ArrayList<String>();
    for (int i = 0; i < 10; i++) {
      links.add("link" + i);
    }
    dummySleep(1000);
    return links;
  }

  private static CompletableFuture<String> readPage() {
    return CompletableFuture.supplyAsync(new Supplier<String>() {
      @Override
      public String get() {
        System.out.println("Getting page...");
        dummySleep(1000);
        return "page";
      }
    });
  }

  private static void dummySleep(int ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      e.printStackTrace();
      Thread.currentThread().interrupt();
    }
  }


  private static CompletableFuture<List<String>> findByName(String name) {
    CompletableFuture<List<String>> accounts;
    if ("One".equals(name)) {
      accounts = CompletableFuture.supplyAsync(() -> {
        dummySleep(1000);
        return asList("1", "one");
      });
    } else if ("Two".equals(name)) {
      accounts = CompletableFuture.supplyAsync(() -> {
        dummySleep(1000);
        return asList("2", "two");
      });
    } else if ("Three".equals(name)) {
      accounts = CompletableFuture.supplyAsync(() -> {
        dummySleep(1000);
        return asList("3", "three");
      });
    } else {
      accounts = CompletableFuture.supplyAsync(() -> {
        dummySleep(1000);
        throw new RuntimeException("UNknown name");
      });
    }

    return accounts;
  }

  private static CompletableFuture<Map<String, String>> updateInfo(int amt, String id) {
    CompletableFuture<Map<String, String>> futureData;

    Map<String, String> data = new HashMap<>();
    data.put("id", id);

    if ("1".equals(id)) {
      futureData = CompletableFuture.supplyAsync(() -> {
        dummySleep(1000);
        data.put("amount", 100000 + amt + "");
        return data;
      });
    } else if ("2".equals(id)) {
      futureData = CompletableFuture.supplyAsync(() -> {
        dummySleep(1000);
        data.put("amount", 200000 + amt + "");
        return data;
      });
    } else if ("3".equals(id)) {
      futureData = CompletableFuture.supplyAsync(() -> {
        dummySleep(1000);
        data.put("amount", 300000 + amt + "");
        return data;
      });
    } else {
      futureData = CompletableFuture.supplyAsync(() -> {
        dummySleep(1000);
        throw new RuntimeException("UNknown name");
      });
    }

    return futureData;
  }
}
