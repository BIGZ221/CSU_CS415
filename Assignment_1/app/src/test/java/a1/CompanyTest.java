package a1;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

public class CompanyTest {
  private String defaultName;
  private Company company;

  @BeforeEach
  void setupCompany() {
    defaultName = "name";
    company = new Company(defaultName);
  }

  @ParameterizedTest
  @NullAndEmptySource
  void nullAndEmptyNameThrowsIllegalArgument(String name) {
    assertThrows(RuntimeException.class, () -> new Company(name));
  }

  @ParameterizedTest
  @CsvSource({ "name, true", "not, false" })
  void companyEquals(String name, Boolean result) {
    Company a = new Company(name);
    if (result) {
      assertEquals(this.company, a, "Companies are not equal when they should be.");
    } else {
      assertNotEquals(this.company, a, "Companies are equal when they shouldn't be.");
    }
  }

  @Test
  void companyNotEqualsOtherObject() {
    Object x = new Object();
    assertNotEquals(this.company, x);
  }

  @Test
  void stringifyDefaultCorrectly() {
    String expected = "name:0:0";
    assertEquals(expected, company.toString());
  }

  @Test
  void nameConstructsCorrectly() {
    assertEquals(defaultName, company.getName());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void setNameThrowsIllegalArgumentIfEmptyOrNull(String newName) {
    assertThrows(RuntimeException.class, () -> company.setName(newName));
  }

  @Test
  void setNameCorrectly() {
    String expected = "new name";
    company.setName(expected);
    assertEquals(expected, company.getName());
  }

  @Test
  void defaultEmployedWorkers() {
    assertTrue(company.getEmployedWorkers().isEmpty());
  }

  @Test
  void defaultAvailableWorkers() {
    assertTrue(company.getAvailableWorkers().isEmpty());
  }

  @Test
  void defaultAssignedWorkers() {
    assertTrue(company.getAssignedWorkers().isEmpty());
  }

  @Test
  void defaultProjects() {
    assertTrue(company.getProjects().isEmpty());
  }

  @Test
  void defaultQualifications() {
    assertTrue(company.getQualifications().isEmpty());
  }

  @ParameterizedTest
  @NullAndEmptySource
  void createQualificationWithNullOrEmptyDescReturnsNull(String desc) {
    assertNull(company.createQualification(desc));
  }

  @Test
  @DisplayName("Valid qualification gets added/created correctly")
  void createQualificationValidQualification() {
    Qualification q = company.createQualification("test");
    assertNotNull(q);
    assertTrue(company.getQualifications().contains(q));
  }

  @ParameterizedTest
  @NullAndEmptySource
  void createWorkerWithNullOrEmptyNameReturnsNull(String name) {
    Qualification q = new Qualification("pizza");
    Set<Qualification> workerQuals = new HashSet<>();
    workerQuals.add(q);
    assertNull(company.createWorker(name, workerQuals, 12.0));
  }

  @Test
  void createWorkerWithExtraQualificationsReturnsNull() {
    Qualification x = new Qualification("pizza");
    Set<Qualification> quals = new HashSet<>();
    quals.add(x);
    assertNull(company.createWorker("jerry", quals, 123.0));
  }

  @Test
  void createWorkerWithNoQualificationReturnsNull() {
    assertNull(company.createWorker("jerry", new HashSet<>(), 123.0));
  }

  @Test
  void createWorkerAddedToAvailableEmployedWorker() {
    Qualification q = company.createQualification("test");
    Set<Qualification> quals = new HashSet<>();
    quals.add(q);
    Worker w = company.createWorker("tim", quals, 123.34);
    assertTrue(company.getAvailableWorkers().contains(w));
    assertTrue(company.getEmployedWorkers().contains(w));
  }

  @Test
  void createWorkerAddedToAllQualificationsWorkerHas() {
    Qualification q = company.createQualification("test");
    Qualification q1 = company.createQualification("jim");
    Set<Qualification> quals = new HashSet<>();
    quals.add(q);
    quals.add(q1);
    Worker w = company.createWorker("tim", quals, 123.34);
    assertTrue(q.getWorkers().contains(w));
    assertTrue(q1.getWorkers().contains(w));
  }

  @ParameterizedTest
  @NullAndEmptySource
  void createProjectWithNullOrEmptyNameReturnsNull(String name) {
    Qualification q = new Qualification("pizza");
    Set<Qualification> projectQuals = new HashSet<>();
    projectQuals.add(q);
    assertNull(company.createProject(name, projectQuals, ProjectSize.SMALL));
  }

  @Test
  void createProjectAddedToCompanyProjects() {
    Qualification q = new Qualification("pizza");
    Set<Qualification> projectQuals = new HashSet<>();
    projectQuals.add(q);
    Project p = company.createProject("panda", projectQuals, ProjectSize.SMALL);
    assertTrue(company.getProjects().contains(p));
  }

  @Test
  void assignWorkerNullThrowsNullPointer() {
    Qualification q = company.createQualification("pizza");
    Set<Qualification> projectQuals = new HashSet<>();
    projectQuals.add(q);
    Project p = company.createProject("panda", projectQuals, ProjectSize.SMALL);
    assertThrows(RuntimeException.class, () -> company.assign(null, p));
  }

  @Test
  void assignProjectNullThrowsNullPointer() {
    Qualification q = company.createQualification("pizza");
    Set<Qualification> workerQuals = new HashSet<>();
    workerQuals.add(q);
    Worker w = company.createWorker("name", workerQuals, 33.33);
    assertThrows(RuntimeException.class, () -> company.assign(w, null));
  }

  @Test
  void assignWorkerToProjectSuccess() {
    Qualification q = company.createQualification("a");
    Set<Qualification> quals = new HashSet<>();
    quals.add(q);
    Project p = company.createProject("none", quals, ProjectSize.SMALL);
    Worker w = company.createWorker("jerry", quals, 123.2);
    company.assign(w, p);
    assertAll(
        () -> assertTrue(p.getWorkers().contains(w)),
        () -> assertTrue(w.getProjects().contains(p)),
        () -> assertTrue(company.getAssignedWorkers().contains(w)),
        () -> assertTrue(company.getAvailableWorkers().contains(w)),
        () -> assertEquals(1, w.getWorkload()));
  }

  @Test
  void assignWorkerToProjectWorkerRemovedFromAvailable() {
    Qualification q = company.createQualification("a");
    Set<Qualification> quals = new HashSet<>();
    quals.add(q);
    Worker w = company.createWorker("jerry", quals, 123.2);
    for (int i = 0; i < 12; i++) {
      Project p = company.createProject(Integer.toString(i), quals, ProjectSize.SMALL);
      company.assign(w, p);
    }
    assertAll(
        () -> assertTrue(company.getAssignedWorkers().contains(w)),
        () -> assertFalse(company.getAvailableWorkers().contains(w)),
        () -> assertEquals(12, w.getWorkload()));
  }

  @Test
  void assignWorkerToProjectFailsIfWorkerIsNotAvailable() {
    Qualification q = company.createQualification("a");
    Set<Qualification> quals = new HashSet<>();
    quals.add(q);
    Worker w = company.createWorker("jerry", quals, 123.2);
    for (int i = 0; i < 12; i++) {
      Project p = company.createProject(Integer.toString(i), quals, ProjectSize.SMALL);
      company.assign(w, p);
    }
    Project p = company.createProject("fail", quals, ProjectSize.SMALL);
    assertThrows(RuntimeException.class, () -> company.assign(w, p));
  }

  @Test
  void assignWorkerToProjectFailsIfWorkerAlreadyAssigned() {
    Qualification q = company.createQualification("a");
    Set<Qualification> quals = new HashSet<>();
    quals.add(q);
    Worker w = company.createWorker("jerry", quals, 123.2);
    Project p = company.createProject("fail", quals, ProjectSize.SMALL);
    company.assign(w, p);
    assertThrows(RuntimeException.class, () -> company.assign(w, p));
  }

  @ParameterizedTest
  @EnumSource(names = { "ACTIVE", "FINISHED" })
  void assignWorkerToProjectFailsIfProjectIsActiveOrFinished(ProjectStatus status) {
    Qualification q = company.createQualification("a");
    Set<Qualification> quals = new HashSet<>();
    quals.add(q);
    Worker w = company.createWorker("jerry", quals, 123.2);
    Project p = company.createProject("fail", quals, ProjectSize.SMALL);
    p.setStatus(status);
    assertThrows(RuntimeException.class, () -> company.assign(w, p));
  }

  @Test
  void assignWorkerToProjectFailsIfWorkerWouldOverload() {
    Qualification q = company.createQualification("a");
    Set<Qualification> quals = new HashSet<>();
    quals.add(q);
    Worker w = company.createWorker("jerry", quals, 123.2);
    for (int i = 0; i < 12; i++) {
      Project p = company.createProject(Integer.toString(i), quals, ProjectSize.SMALL);
      company.assign(w, p);
    }
    Project p = company.createProject("fail", quals, ProjectSize.SMALL);
    assertThrows(RuntimeException.class, () -> company.assign(w, p));
  }

  @Test
  void assignWorkerToProjectFailsIfWorkerIsNotHelpful() {
    Qualification q = company.createQualification("a");
    Qualification q1 = company.createQualification("b");
    Set<Qualification> projectQualifications = new HashSet<>();
    projectQualifications.add(q);
    Set<Qualification> workerQualifications = new HashSet<>();
    workerQualifications.add(q1);
    Worker w = company.createWorker("jerry", workerQualifications, 123.2);
    Project p = company.createProject("something", projectQualifications, ProjectSize.SMALL);
    assertThrows(RuntimeException.class, () -> company.assign(w, p));
  }

  @Test
  void startNullProjectThrowsNullPointer() {
    assertThrows(RuntimeException.class, () -> company.start(null));
  }

  @Test
  void startProjectDoesNothingIfCannotStart() {
    Qualification q = new Qualification("pizza");
    Set<Qualification> projectQuals = new HashSet<>();
    projectQuals.add(q);
    Project p = company.createProject("panda", projectQuals, ProjectSize.SMALL);
    company.start(p);
    assertEquals(ProjectStatus.PLANNED, p.getStatus());
  }

  @Test
  void startProjectIfAllQualificationsMet() {
    Qualification q = company.createQualification("pizza");
    Set<Qualification> projectQuals = new HashSet<>();
    projectQuals.add(q);
    Project p = company.createProject("panda", projectQuals, ProjectSize.SMALL);
    Worker w = company.createWorker("jerry", projectQuals, 123.33);
    p.addWorker(w);
    company.start(p);
    assertEquals(p.getStatus(), ProjectStatus.ACTIVE);
  }

  @Test
  void unassignNullWorkerThrowsNullPointer() {
    Qualification q = company.createQualification("pizza");
    Set<Qualification> projectQuals = new HashSet<>();
    projectQuals.add(q);
    Project p = company.createProject("panda", projectQuals, ProjectSize.SMALL);
    assertThrows(RuntimeException.class, () -> company.unassign(null, p));
  }

  @Test
  void unassignNullProjectThrowsNullPointer() {
    Qualification q = company.createQualification("pizza");
    Set<Qualification> workerQuals = new HashSet<>();
    workerQuals.add(q);
    Worker w = company.createWorker("name", workerQuals, 123.3);
    assertThrows(RuntimeException.class, () -> company.unassign(w, null));
  }

  @Test
  void unassignNonAssignedWorkerThrowsIllegalArgument() {
    Qualification q = company.createQualification("pizza");
    Set<Qualification> projectQuals = new HashSet<>();
    projectQuals.add(q);
    Project p = company.createProject("panda", projectQuals, ProjectSize.SMALL);
    Worker w = company.createWorker("jerry", projectQuals, 123.33);
    assertThrows(RuntimeException.class, () -> company.unassign(w, p));
  }

  @Test
  void projectIsRemovedFromWorkerAndWorkerRemovedFromProjectWorkerStillAssigned() {
    Qualification q = company.createQualification("pizza");
    Set<Qualification> projectQuals = new HashSet<>();
    projectQuals.add(q);
    Project p = company.createProject("panda", projectQuals, ProjectSize.SMALL);
    Project p1 = company.createProject("temp", projectQuals, ProjectSize.SMALL);
    Worker w = company.createWorker("jerry", projectQuals, 123.33);
    company.assign(w, p);
    company.assign(w, p1);
    assumeTrue(company.getAssignedWorkers().contains(w));
    company.unassign(w, p);
    assertAll(
        () -> assertTrue(company.getAssignedWorkers().contains(w)),
        () -> assertTrue(w.getProjects().contains(p1)),
        () -> assertTrue(p1.getWorkers().contains(w)),
        () -> assertFalse(w.getProjects().contains(p)),
        () -> assertFalse(p.getWorkers().contains(w)));
  }

  @Test
  void workerRemovedFromAssignedIfProjectWasOnlyAssigned() {
    Qualification q = company.createQualification("pizza");
    Set<Qualification> projectQuals = new HashSet<>();
    projectQuals.add(q);
    Project p = company.createProject("panda", projectQuals, ProjectSize.SMALL);
    Worker w = company.createWorker("jerry", projectQuals, 123.33);
    company.assign(w, p);
    assumeTrue(company.getAssignedWorkers().contains(w));
    company.unassign(w, p);
    assertFalse(company.getAssignedWorkers().contains(w));
  }

  @Test
  void unassignUnavailableWorkerBecomesAvailable() {
    Qualification q = company.createQualification("a");
    Set<Qualification> quals = new HashSet<>();
    quals.add(q);
    Worker w = company.createWorker("jerry", quals, 123.2);
    for (int i = 0; i < 11; i++) {
      Project p = company.createProject(Integer.toString(i), quals, ProjectSize.SMALL);
      company.assign(w, p);
    }
    Project p = company.createProject("hello", quals, ProjectSize.SMALL);
    company.assign(w, p);
    assumeFalse(w.isAvailable());
    assumeFalse(company.getAvailableWorkers().contains(w));
    company.unassign(w, p);
    assertTrue(w.isAvailable());
    assertTrue(company.getAvailableWorkers().contains(w));
  }

  @ParameterizedTest
  @EnumSource(names = { "PLANNED", "SUSPENDED" })
  void projectStatusUnaffectedIfNotAllQualsMetAfterUnassign(ProjectStatus status) {
    Qualification q = company.createQualification("a");
    Set<Qualification> quals = new HashSet<>();
    quals.add(q);
    Worker w = company.createWorker("jerry", quals, 123.2);
    Project p = company.createProject("hello", quals, ProjectSize.SMALL);
    company.assign(w, p);
    p.setStatus(status);
    company.unassign(w, p);
    assertEquals(status, p.getStatus());
  }

  @Test
  void projectStatusGetsSuspendedIfActiveAndNotAllQualsMetAfterUnassign() {
    Qualification q = company.createQualification("a");
    Set<Qualification> quals = new HashSet<>();
    quals.add(q);
    Worker w = company.createWorker("jerry", quals, 123.2);
    Project p = company.createProject("hello", quals, ProjectSize.SMALL);
    company.assign(w, p);
    company.start(p);
    assumeTrue(p.getStatus() == ProjectStatus.ACTIVE);
    company.unassign(w, p);
    assertEquals(ProjectStatus.SUSPENDED, p.getStatus());
  }

  @Test
  void unassignAllNullWorkerThrowsNullPointer() {
    assertThrows(RuntimeException.class, () -> company.unassignAll(null));
  }

  @Test
  void unassignAllWorkerRemovedFromAllAssignedProjects() {
    Qualification q = company.createQualification("a");
    Set<Qualification> quals = new HashSet<>();
    quals.add(q);
    Worker w = company.createWorker("jerry", quals, 123.3);
    for (int i = 0; i < 5; i++) {
      Project p = company.createProject(String.valueOf(i), quals, ProjectSize.SMALL);
      company.assign(w, p);
    }
    Set<Project> projects = company.getProjects();
    assumeTrue(company.getAssignedWorkers().contains(w));
    for (Project project : projects) {
      assertTrue(project.getWorkers().contains(w));
    }
    company.unassignAll(w);

    for (Project project : projects) {
      assertFalse(project.getWorkers().contains(w));
    }
    assertEquals(0, w.getWorkload());
  }

  @Test
  void defaultUnavailableWorkers() {
    assertTrue(company.getUnavailableWorkers().isEmpty());
  }

  @Test
  void workerIsUnassignedWhenNotAssignedToAnyProjects() {
    Qualification q = company.createQualification("a");
    Set<Qualification> quals = new HashSet<>();
    quals.add(q);
    Worker w = company.createWorker("jerry", quals, 123.3);
    for (int i = 0; i < 12; i++) {
      company.createProject(String.valueOf(i), quals, ProjectSize.SMALL);
    }
    assertTrue(company.getUnassignedWorkers().contains(w));
  }

  @Test
  void unassignedWorkersIsEmptyIfAllAreAssigned() {
    Qualification q = company.createQualification("a");
    Qualification q1 = company.createQualification("b");
    Set<Qualification> quals = new HashSet<>();
    Set<Qualification> quals1 = new HashSet<>();
    Set<Qualification> quals2 = new HashSet<>();
    quals.add(q);
    quals1.add(q1);
    quals2.add(q);
    quals2.add(q1);
    Worker w = company.createWorker("jerry", quals, 123.3);
    Worker w1 = company.createWorker("terry", quals1, 123.3);
    Project p = company.createProject("t", quals2, ProjectSize.MEDIUM);
    company.assign(w, p);
    company.assign(w1, p);
    assertTrue(company.getUnassignedWorkers().isEmpty());
  }

  @Test
  void defaultUnassignedWorkers() {
    assertTrue(company.getUnassignedWorkers().isEmpty());
  }

  @Test
  void unavailableIsEmptyWithNoWorkersAtFullLoad() {
    Qualification q = company.createQualification("a");
    Set<Qualification> quals = new HashSet<>();
    quals.add(q);
    Worker w = company.createWorker("jerry", quals, 123.3);
    Project p = company.createProject("nope", quals, ProjectSize.SMALL);
    company.assign(w, p);
    assertTrue(company.getUnavailableWorkers().isEmpty());
  }

  @Test
  void workerIsUnavailableWithFullWorkload() {
    Qualification q = company.createQualification("a");
    Set<Qualification> quals = new HashSet<>();
    quals.add(q);
    Worker w = company.createWorker("jerry", quals, 123.3);
    for (int i = 0; i < 12; i++) {
      Project p = company.createProject(String.valueOf(i), quals, ProjectSize.SMALL);
      company.assign(w, p);
    }
    assertTrue(company.getUnavailableWorkers().contains(w));
  }

  @Test
  void finishNullProjectThrowsNullPointer() {
    assertThrows(RuntimeException.class, () -> company.finish(null));
  }

  @Test
  void finishProjectSetsProjectStatusToFinished() {
    Qualification q = company.createQualification("pizza");
    Set<Qualification> quals = new HashSet<>();
    quals.add(q);
    Worker w = company.createWorker("jerry", quals, 123.1);
    Project p = company.createProject("thing", quals, ProjectSize.SMALL);
    company.assign(w, p);
    company.start(p);
    assumeTrue(p.getStatus() == ProjectStatus.ACTIVE);
    company.finish(p);
    assertEquals(ProjectStatus.FINISHED, p.getStatus());
  }

  @ParameterizedTest
  @EnumSource(names = { "PLANNED", "SUSPENDED", "FINISHED" })
  void finishNonActiveProjectStaysCurrentStatus(ProjectStatus status) {
    Qualification q = company.createQualification("pizza");
    Set<Qualification> quals = new HashSet<>();
    quals.add(q);
    Worker w = company.createWorker("jerry", quals, 123.1);
    Project p = company.createProject("thing", quals, ProjectSize.SMALL);
    company.assign(w, p);
    p.setStatus(status);
    company.finish(p);
    assertEquals(status, p.getStatus());
  }

  @Test
  void finishProjectRemovesAllAssignedWorkers() {
    Set<Qualification> quals = new HashSet<>();
    for (char i = 'a'; i < 'a' + 12; i++) {
      quals.add(company.createQualification(String.valueOf(i)));
    }
    Set<Worker> workers = new HashSet<>();
    Project p = company.createProject("test", quals, ProjectSize.SMALL);
    Iterator<Qualification> qIterator = quals.iterator();
    for (int i = 0; i < 12; i++) {
      Set<Qualification> workerQuals = new HashSet<>();
      workerQuals.add(qIterator.next());
      Worker w = company.createWorker(String.valueOf(i), workerQuals, 123.3);
      company.assign(w, p);
      workers.add(w);
    }
    company.start(p);
    company.finish(p);
    assertTrue(p.getWorkers().isEmpty());
  }

  @Test
  void finishProjectRemovesProjectFromAllWorkers() {
    Set<Qualification> quals = new HashSet<>();
    for (char i = 'a'; i < 'a' + 12; i++) {
      quals.add(company.createQualification(String.valueOf(i)));
    }
    Set<Worker> workers = new HashSet<>();
    Project p = company.createProject("test", quals, ProjectSize.SMALL);
    Iterator<Qualification> qIterator = quals.iterator();
    for (int i = 0; i < 12; i++) {
      Set<Qualification> workerQuals = new HashSet<>();
      workerQuals.add(qIterator.next());
      Worker w = company.createWorker(String.valueOf(i), workerQuals, 123.3);
      company.assign(w, p);
      workers.add(w);
    }
    company.start(p);
    company.finish(p);
    for (Worker w : workers) {
      assertFalse(w.getProjects().contains(p));
    }
    assertTrue(p.getWorkers().isEmpty());
  }

}
