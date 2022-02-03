package a1;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

public class ProjectTest {

  private String defaultName;
  private Set<Qualification> defaultQualifications;
  private ProjectSize defaultSize;
  private Project project;

  @BeforeEach
  void setupProject() {
    defaultName = "name";
    Qualification q = new Qualification("coffee");
    defaultQualifications = new HashSet<>();
    defaultQualifications.add(q);
    defaultSize = ProjectSize.SMALL;
    project = new Project(defaultName, defaultQualifications, defaultSize);
  }

  @ParameterizedTest
  @NullAndEmptySource
  void constructorThrowsIllegalArgumentOnNullEmptyName(String name) {
    assertThrows(RuntimeException.class, () -> new Project(name, defaultQualifications, ProjectSize.SMALL));
  }

  @Test
  void constructorThrowsNullPointerOnNullQualifications() {
    assertThrows(RuntimeException.class, () -> new Project("test",
        null, ProjectSize.SMALL));
  }

  @Test
  void constructorThrowsIllegalArgumentOnEmptyQualifications() {
    assertThrows(RuntimeException.class, () -> new Project("test", new HashSet<>(), ProjectSize.SMALL));
  }

  @Test
  void constructorThrowsNullPointerOnNullSize() {
    assertThrows(RuntimeException.class, () -> new Project("test",
        defaultQualifications, null));
  }

  @ParameterizedTest
  @CsvSource({ "name, name, true", "name, not, false" })
  void projectEquality(String name, String expected, Boolean isEqual) {
    Project a = new Project(expected, defaultQualifications, defaultSize);
    Project b = new Project(name, defaultQualifications, defaultSize);
    if (isEqual) {
      assertEquals(a, b);
    } else {
      assertNotEquals(a, b);
    }
  }

  @Test
  void projectNotEqualNonProject() {
    assertNotEquals(project, new Object());
  }

  @Test
  void stringifyCorrectly() {
    String expected = "name:0:PLANNED";
    assertEquals(expected, project.toString());
  }

  @Test
  void getNameReturnsCorrectName() {
    assertEquals(defaultName, project.getName());
  }

  @Test
  void setNameThenGetNameEqual() {
    String newName = "jerry";
    project.setName(newName);
    assertEquals(newName, project.getName());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void setNameWithNullOrEmptyThrowsIllegalArgument(String newName) {
    assertThrows(RuntimeException.class, () -> project.setName(newName));
  }

  @Test
  void getSizeReturnsCorrectSize() {
    assertEquals(defaultSize, project.getSize());
  }

  @Test
  void setSizeNullThrowsNullPointer() {
    assertThrows(RuntimeException.class, () -> project.setSize(null));
  }

  @ParameterizedTest
  @EnumSource(ProjectSize.class)
  void setSizeAndGetSize(ProjectSize size) {
    project.setSize(size);
    assertEquals(size, project.getSize());
  }

  @Test
  void getSizeReturnsCorrectStatus() {
    assertEquals(ProjectStatus.PLANNED, project.getStatus());
  }

  @Test
  void setStatusNullThrowsNullPointer() {
    assertThrows(RuntimeException.class, () -> project.setStatus(null));
  }

  @ParameterizedTest
  @EnumSource(ProjectStatus.class)
  void setStatusAndGetStatus(ProjectStatus status) {
    project.setStatus(status);
    assertEquals(status, project.getStatus());
  }

  @Test
  void defaultWorkers() {
    assertTrue(project.getWorkers().isEmpty());
  }

  @Test
  void addNullWorkerThrowsNullPointer() {
    assertThrows(RuntimeException.class, () -> project.addWorker(null));
  }

  @Test
  void addWorkersResultsInCorrectSet() {
    Worker w = new Worker("name", defaultQualifications, 123.3);
    project.addWorker(w);
    assertTrue(project.getWorkers().contains(w));
  }

  @Test
  void duplicateAddWorkerThrowsIllegalArgument() {
    Worker w = new Worker("name", defaultQualifications, 123.3);
    project.addWorker(w);
    assertThrows(RuntimeException.class, () -> project.addWorker(w));
  }

  @Test
  void removeNullWorkerThrowsNullPointer() {
    assertThrows(RuntimeException.class, () -> project.removeWorker(null));
  }

  @Test
  void removeUnknownWorkerThrowsIllegalArgument() {
    Worker w = new Worker("name", defaultQualifications, 123.3);
    assertThrows(RuntimeException.class, () -> project.removeWorker(w));
  }

  @Test
  void workerIsRemovedCorrectly() {
    Worker w = new Worker("name", defaultQualifications, 123.3);
    project.addWorker(w);
    project.removeWorker(w);
    assertTrue(project.getWorkers().isEmpty());
  }

  @Test
  void removeAllWorkersRemovesAll() {
    Worker w = new Worker("name", defaultQualifications, 123.3);
    project.addWorker(w);
    assumeTrue(project.getWorkers().size() == 1);
    project.removeAllWorkers();
    assertTrue(project.getWorkers().isEmpty());
  }

  @Test
  void defaultQualifications() {
    assertEquals(defaultQualifications, project.getRequiredQualifications());
  }

  @Test
  void addNullQualificationThrowsNullPointer() {
    assertThrows(RuntimeException.class, () -> project.addQualification(null));
  }

  @Test
  void qualificationAddedCorrectly() {
    Qualification q = new Qualification("192");
    project.addQualification(q);
    assertTrue(project.getRequiredQualifications().contains(q));
  }

  @Test
  void duplicateQualificationThrowsIllegalArgument() {
    Qualification q = new Qualification("192");
    project.addQualification(q);
    assertThrows(RuntimeException.class, () -> project.addQualification(q));
  }

  @Test
  void getMissingQualificationsDefault() {
    assertEquals(defaultQualifications, project.getMissingQualifications());
  }

  @Test
  void noMissingQualificationsReturnsEmpty() {
    Worker w = new Worker("name", defaultQualifications, 12.3);
    project.addWorker(w);
    assertTrue(project.getMissingQualifications().isEmpty());
  }

  @Test
  void noQualificationsAreMetByWorkers() {
    Set<Qualification> workerQuals = new HashSet<>();
    workerQuals.add(new Qualification("eating"));
    project.addWorker(new Worker("tim", workerQuals, 11.0));
    assertEquals(defaultQualifications, project.getMissingQualifications());
  }

  @Test
  void isHelpfulNullWorkerThrowsNullPointer() {
    assertThrows(RuntimeException.class, () -> project.isHelpful(null));
  }

  private static Stream<Arguments> helpfulWorkerArgumentProvider() {
    Set<Qualification> uselessQualifications = new HashSet<>();
    uselessQualifications.add(new Qualification("eating"));
    Set<Qualification> usefulQualifications = new HashSet<>();
    usefulQualifications.add(new Qualification("coffee"));
    return Stream.of(
        Arguments.of(new Worker("jerry", uselessQualifications, 11.1), false),
        Arguments.of(new Worker("phil", usefulQualifications, 45.1), true));
  }

  @ParameterizedTest
  @MethodSource("helpfulWorkerArgumentProvider")
  void workerIsHelpful(Worker w, boolean isHelpful) {
    assertEquals(isHelpful, project.isHelpful(w));
  }

}
