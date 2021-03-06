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

  private Worker worker;
  private String defaultName;
  private Double defaultSalary;
  private Set<Qualification> defaultQualifications;
  private Project testingProject;

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
  void nullOrEmptyNameThrowsIllegalArgument(String name) {
    assertThrows(RuntimeException.class, () -> new Worker(name, defaultQualifications, defaultSalary));
  }

  private static Stream<Arguments> emptyQualificationsSetArgument() {
    return Stream.of(
        Arguments.of(new HashSet<>()));
  }

  @ParameterizedTest
  @NullSource
  @MethodSource("emptyQualificationsSetArgument")
  void nullOrEmptyQualificationsThrowsIllegalArgument(Set<Qualification> qualifications) {
    assertThrows(RuntimeException.class, () -> new Worker(defaultName, qualifications, defaultSalary));
  }

  @ParameterizedTest
  @CsvSource({ "name, true", "not, false" })
  void workerEquals(String name, Boolean result) {
    Worker a = new Worker(name, defaultQualifications, defaultSalary);
    if (result) {
      assertEquals(this.worker, a, "Qualifications are not equal when they should be.");
    } else {
      assertNotEquals(this.worker, a, "Qualifications are equal when they shouldn't be.");
    }
  }

  @Test
  void workerNotEqualsOtherObject() {
    Object x = new Object();
    assertNotEquals(this.worker, x);
  }

  @ParameterizedTest
  @CsvSource({ "12.0003, 12", "15.9999, 15", "0, 0", "10000.20, 10000" })
  void workerStringifiesCorrectly(double salary, int expectedSalary) {
    Worker a = new Worker(defaultName, defaultQualifications, salary);
    String expected = String.format("name:0:2:%d", expectedSalary);
    assertEquals(expected, a.toString());
  }

  @Test
  void getNameCorrectly() {
    assertEquals(defaultName, worker.getName());
  }

  @Test
  void setNameCorrectly() {
    String newName = "herald";
    worker.setName(newName);
    assertEquals(newName, worker.getName());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void nullOrEmptySetNameThrowsIllegalArgument(String newName) {
    assertThrows(RuntimeException.class, () -> worker.setName(newName));
  }

  @Test
  void getSalaryCorrectly() {
    assertEquals(defaultSalary, worker.getSalary());
  }

  @Test
  void setSalaryCorrectly() {
    double newSalary = 987.654;
    worker.setSalary(newSalary);
    assertEquals(newSalary, worker.getSalary());
  }

  @Test
  void negativeSetSalaryThrowsIllegalArgument() {
    assertThrows(RuntimeException.class, () -> worker.setSalary(-1.2));
  }

  @Test
  void returnsCorrectQualifications() {
    assertEquals(defaultQualifications, worker.getQualifications());
  }

  @Test
  void addNullQualificationThrowsIllegalArgument() {
    assertThrows(RuntimeException.class, () -> worker.addQualification(null));
  }

  @Test
  void addUniqueQualification() {
    worker.addQualification(new Qualification("test"));
    assertEquals(3, worker.getQualifications().size());
  }

  @Test
  void addDuplicateQualificationThrowsIllegalArgumentException() {
    assertThrows(RuntimeException.class,
        () -> worker.addQualification(defaultQualifications.iterator().next()));
  }

  @Test
  void defaultProjects() {
    assertTrue(worker.getProjects().isEmpty());
  }

  @Test
  void nullAddProjectThrowsNullPointer() {
    assertThrows(RuntimeException.class, () -> worker.addProject(null));
  }

  @Test
  void addProjectAddsProjectCorrectly() {
    worker.addProject(testingProject);
    assertTrue(worker.getProjects().contains(testingProject));
  }

  @Test
  void addDuplicateProjectThrowsIllegalArgument() {
    worker.addProject(testingProject);
    assertThrows(RuntimeException.class, () -> worker.addProject(testingProject));
  }

  @Test
  void removeNullProjectThrowsNullPointer() {
    assertThrows(RuntimeException.class, () -> worker.removeProject(null));
  }

  @Test
  void removeProjectRemoveProjectCorrectly() {
    worker.addProject(testingProject);
    worker.removeProject(testingProject);
    assertFalse(worker.getProjects().contains(testingProject));
  }

  @Test
  void defaultWorkload() {
    assertEquals(0, worker.getWorkload());
  }

  private static Stream<Arguments> addingWorkLoadArgumentsProvider() {
    return Stream.of(Arguments.of(ProjectSize.SMALL, 1), Arguments.of(ProjectSize.MEDIUM, 2),
        Arguments.of(ProjectSize.BIG, 3));
  }

  @ParameterizedTest
  @MethodSource("addingWorkLoadArgumentsProvider")
  void addingProjectIncreasesWorkloadByCorrectSize(ProjectSize size, int expectedWorkload) {
    Project p = new Project("test", defaultQualifications, size);
    worker.addProject(p);
    assertEquals(expectedWorkload, worker.getWorkload());
  }

  @ParameterizedTest
  @MethodSource("addingWorkLoadArgumentsProvider")
  void removingProjectDecreasesWorkloadByCorrectSize(ProjectSize size, int workload) {
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
  void willOverloadThrowsNullPointerIfProjectNull() {
    assertThrows(RuntimeException.class, () -> worker.willOverload(null));
  }

  @Test
  void willNotOverload() {
    assertFalse(worker.willOverload(new Project("tt", defaultQualifications, ProjectSize.SMALL)));
  }

  @ParameterizedTest
  @MethodSource("willOverloadArgumentsProvider")
  void workerWillOverloadWithCorrectProjects(ProjectSize size, int projectsToOverload) {
    for (int i = 0; i < projectsToOverload - 1; i++) {
      worker.addProject(new Project(String.valueOf(i), defaultQualifications, size));
    }
    assertTrue(worker.willOverload(new Project("overload", defaultQualifications, size)));
  }

  @Test
  void willOverloadDuplicateThrowsIllegalArgument() {
    Project dupe = new Project("test", defaultQualifications, ProjectSize.MEDIUM);
    worker.addProject(dupe);
    assertThrows(RuntimeException.class, () -> worker.willOverload(dupe));
  }

  @Test
  void isAvailableByDefault() {
    assertTrue(worker.isAvailable());
  }

  @Test
  void notAvailableIfWorkloadIsTwelve() {
    for (int i = 0; i < 12; i++) {
      worker.addProject(new Project(String.valueOf(i), defaultQualifications, ProjectSize.SMALL));
    }
    assertFalse(worker.isAvailable());
  }

}
