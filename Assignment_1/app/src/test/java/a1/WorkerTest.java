package a1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

public class WorkerTest {

  Worker worker;
  String defaultName;
  Double defaultSalary;
  Set<Qualification> defaultQualifications;
  Project testingProject;

  @BeforeEach
  void setupWorker() {
    defaultName = "name";
    defaultSalary = 12.34;
    defaultQualifications = new HashSet<>();
    Qualification a = new Qualification("a");
    Qualification b = new Qualification("b");
    defaultQualifications.add(a);
    defaultQualifications.add(b);
    testingProject = new Project("test project", defaultQualifications, ProjectSize.SMALL);
    worker = new Worker(defaultName, defaultQualifications, defaultSalary);
  }

  @ParameterizedTest
  @NullAndEmptySource
  public void nullOrEmptyNameThrowsIllegalArgument(String name) {
    assertThrows(IllegalArgumentException.class, () -> new Worker(null, defaultQualifications, defaultSalary));
  }

  private static Stream<Arguments> emptyQualificationsSetArgument() {
    return Stream.of(
        Arguments.of(new HashSet<>()));
  }

  @ParameterizedTest
  @NullSource
  @MethodSource("emptyQualificationsSetArgument")
  public void nullOrEmptyQualificationsThrowsIllegalArgument(Set<Qualification> qualifications) {
    assertThrows(IllegalArgumentException.class, () -> new Worker(defaultName, qualifications, defaultSalary));
  }

  @ParameterizedTest
  @CsvSource({ "name, true", "not, false" })
  public void workerEquals(String name, Boolean result) {
    Worker a = new Worker(name, defaultQualifications, defaultSalary);
    if (result) {
      assertEquals(this.worker, a, "Qualifications are not equal when they should be.");
    } else {
      assertNotEquals(this.worker, a, "Qualifications are equal when they shouldn't be.");
    }
  }

  @Test
  public void workerNotEqualsOtherObject() {
    Object x = new Object();
    assertNotEquals(this.worker, x);
  }

  @ParameterizedTest
  @CsvSource({ "12.0003, 12", "15.9999, 15", "0, 0", "10000.20, 10000" })
  public void workerStringifiesCorrectly(double salary, int expectedSalary) {
    Worker a = new Worker(defaultName, defaultQualifications, salary);
    String expected = String.format("name:0:2:%d", expectedSalary);
    assertEquals(expected, a.toString());
  }

  @Test
  public void getNameCorrectly() {
    assertEquals(defaultName, worker.getName());
  }

  @Test
  public void setNameCorrectly() {
    String newName = "herald";
    worker.setName(newName);
    assertEquals(newName, worker.getName());
  }

  @ParameterizedTest
  @NullAndEmptySource
  public void nullOrEmptySetNameThrowsIllegalArgument(String newName) {
    assertThrows(IllegalArgumentException.class, () -> worker.setName(newName));
  }

  @Test
  public void getSalaryCorrectly() {
    assertEquals(defaultSalary, worker.getSalary());
  }

  @Test
  public void setSalaryCorrectly() {
    double newSalary = 987.654;
    worker.setSalary(newSalary);
    assertEquals(newSalary, worker.getSalary());
  }

  @Test
  public void negativeSetSalaryThrowsIllegalArgument() {
    assertThrows(IllegalArgumentException.class, () -> worker.setSalary(-1.2));
  }

  @Test
  public void returnsCorrectQualifications() {
    assertEquals(defaultQualifications, worker.getQualifications());
  }

  @Test
  public void addNullQualificationThrowsIllegalArgument() {
    assertThrows(NullPointerException.class, () -> worker.addQualification(null));
  }

  @Test
  public void addUniqueQualification() {
    worker.addQualification(new Qualification("test"));
    assertEquals(3, worker.getQualifications().size());
  }

  @Test
  public void addDuplicateQualificationThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class,
        () -> worker.addQualification(defaultQualifications.iterator().next()));
  }

  @Test
  public void defaultProjects() {
    assertTrue(worker.getProjects().isEmpty());
  }

  @Test
  public void nullAddProjectThrowsNullPointer() {
    assertThrows(NullPointerException.class, () -> worker.addProject(null));
  }

  @Test
  public void addProjectAddsProjectCorrectly() {
    worker.addProject(testingProject);
    assertTrue(worker.getProjects().contains(testingProject));
  }

  @Test
  public void addDuplicateProjectThrowsIllegalArgument() {
    worker.addProject(testingProject);
    assertThrows(IllegalArgumentException.class, () -> worker.addProject(testingProject));
  }

  @Test
  public void removeNullProjectThrowsNullPointer() {
    assertThrows(NullPointerException.class, () -> worker.removeProject(null));
  }

  @Test
  public void removeProjectRemoveProjectCorrectly() {
    worker.addProject(testingProject);
    worker.removeProject(testingProject);
    assertFalse(worker.getProjects().contains(testingProject));
  }

  @Test
  public void defaultWorkload() {
    assertEquals(0, worker.getWorkload());
  }

  private static Stream<Arguments> addingWorkLoadArgumentsProvider() {
    return Stream.of(Arguments.of(ProjectSize.SMALL, 1), Arguments.of(ProjectSize.MEDIUM, 2),
        Arguments.of(ProjectSize.BIG, 3));
  }

  @ParameterizedTest
  @MethodSource("addingWorkLoadArgumentsProvider")
  public void addingProjectIncreasesWorkloadByCorrectSize(ProjectSize size, int expectedWorkload) {
    Project p = new Project("test", defaultQualifications, size);
    worker.addProject(p);
    assertEquals(expectedWorkload, worker.getWorkload());
  }

  @ParameterizedTest
  @MethodSource("addingWorkLoadArgumentsProvider")
  public void removingProjectDecreasesWorkloadByCorrectSize(ProjectSize size, int workload) {
    Project p = new Project("test", defaultQualifications, size);
    worker.addProject(p);
    assumeTrue(worker.getWorkload() == workload);
    worker.removeProject(p);
    assertEquals(0, worker.getWorkload());
  }

  private static Stream<Arguments> willOverloadArgumentsProvider() {
    return Stream.of(Arguments.of(ProjectSize.SMALL, 13), Arguments.of(ProjectSize.MEDIUM, 7),
        Arguments.of(ProjectSize.BIG, 5));
  }

  @Test
  public void willOverloadThrowsNullPointerIfProjectNull() {
    assertThrows(NullPointerException.class, () -> worker.willOverload(null));
  }

  @ParameterizedTest
  @MethodSource("willOverloadArgumentsProvider")
  public void workerWillOverloadWithCorrectProjects(ProjectSize size, int projectsToOverload) {
    for (int i = 0; i < projectsToOverload - 1; i++) {
      worker.addProject(new Project(String.valueOf(i), defaultQualifications, size));
    }
    assertTrue(worker.willOverload(new Project("overload", defaultQualifications, size)));
  }

  @Test
  public void willOverloadDuplicateThrowsIllegalArgument() {
    Project dupe = new Project("test", defaultQualifications, ProjectSize.MEDIUM);
    worker.addProject(dupe);
    assertThrows(IllegalArgumentException.class, () -> worker.willOverload(dupe));
  }

  @Test
  public void isAvailableByDefault() {
    assertTrue(worker.isAvailable());
  }

  @Test
  public void notAvailableIfWorkloadIsTwelve() {
    for (int i = 0; i < 12; i++) {
      worker.addProject(new Project(String.valueOf(i), defaultQualifications, ProjectSize.SMALL));
    }
    assertFalse(worker.isAvailable());
  }

}
