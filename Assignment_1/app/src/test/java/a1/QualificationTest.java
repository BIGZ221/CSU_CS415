package a1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

public class QualificationTest {
  private Qualification qualification;
  private String description;
  private Set<Qualification> workerQualifications;

  @BeforeEach
  void setupQualification() {
    description = "name";
    qualification = new Qualification(description);
    workerQualifications = new HashSet<>();
    workerQualifications.add(qualification);
  }

  @ParameterizedTest
  @DisplayName("Throws IllegalArgumentException if description is null or empty")
  @NullAndEmptySource
  public void constructorWithNullOrEmpty(String desc) {
    assertThrows(IllegalArgumentException.class, () -> new Qualification(desc));
  }

  @ParameterizedTest
  @CsvSource({ "name, true", "not, false" })
  public void qualificationEquals(String desc, Boolean result) {
    Qualification a = new Qualification(desc);
    if (result) {
      assertEquals(this.qualification, a, "Qualifications are not equal when they should be.");
    } else {
      assertNotEquals(this.qualification, a, "Qualifications are equal when they shouldn't be.");
    }
  }

  @Test
  public void qualificationNotEqualsOtherObject() {
    Object x = new Object();
    assertNotEquals(this.qualification, x);
  }

  @Test
  public void stringifiesCorrectly() {
    assertEquals(description, qualification.toString());
  }

  @Test
  public void defaultWorkers() {
    assertTrue(qualification.getWorkers().isEmpty());
  }

  @Test
  public void nullAddWorkerThrowsNullPointer() {
    assertThrows(NullPointerException.class, () -> qualification.addWorker(null));
  }

  @ParameterizedTest
  @CsvSource({ "a, 1", "a;b, 2", "a;a, 1" })
  public void addWorkers(String names, int expectedWorkers) {
    String[] workerNames = names.split(";");
    for (String name : workerNames) {
      Worker w = new Worker(name, workerQualifications, 123.0);
      qualification.addWorker(w);
    }
    assertEquals(expectedWorkers, qualification.getWorkers().size());
  }

  @Test
  public void nullRemoveWorkerThrowsNullPointer() {
    assertThrows(NullPointerException.class, () -> qualification.removeWorker(null));
  }

  @ParameterizedTest
  @CsvSource({ "a, a, 0, ;", "a;b;c, b;c, 1, a", "a;b,;, 2,a;b" })
  public void removeWorkers(String namesToAdd, String namesToRemove, int expectedWorkers, String expectedWorkerNames) {
    String[] addedWorkerNames = namesToAdd.split(";");
    String[] removedWorkerNames = namesToRemove.split(";");
    String[] expectedWorkersToExist = expectedWorkerNames.split(";");
    Map<String, Worker> workers = new HashMap<>();
    for (String name : addedWorkerNames) {
      Worker w = new Worker(name, workerQualifications, 123.0);
      workers.put(name, w);
      qualification.addWorker(w);
    }
    for (String name : removedWorkerNames) {
      qualification.removeWorker(workers.get(name));
    }
    assertEquals(expectedWorkers, qualification.getWorkers().size());
    for (String name : expectedWorkersToExist) {
      assertTrue(qualification.getWorkers().contains(workers.get(name)));
    }
  }

}