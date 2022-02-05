package a1;

import java.util.Set;
import java.util.HashSet;

public class Company {

  private String name;
  private Set<Worker> employedWorkers;
  private Set<Worker> availableWorkers;
  private Set<Worker> assignedWorkers;
  private Set<Project> projects;
  private Set<Qualification> qualifications;

  public Company(String name) {
    if (name == null || name.isEmpty())
      throw new IllegalArgumentException();
    this.name = name;
    this.employedWorkers = new HashSet<>();
    this.availableWorkers = new HashSet<>();
    this.assignedWorkers = new HashSet<>();
    this.projects = new HashSet<>();
    this.qualifications = new HashSet<>();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Company)) {
      return false;
    }
    Company rhs = (Company) o;
    return name.equals(rhs.getName());
  }

  @Override
  public String toString() {
    return String.format("%s:%d:%d", name, availableWorkers.size(), projects.size());
  }

  public String getName() {
    return name;
  }

  public void setName(String s) {
    if (s == null || s.isEmpty())
      throw new IllegalArgumentException();
    name = s;
  }

  public Set<Worker> getEmployedWorkers() {
    return employedWorkers;
  }

  public Set<Worker> getAvailableWorkers() {
    return availableWorkers;
  }

  public Set<Worker> getUnavailableWorkers() {
    Set<Worker> unavailable = new HashSet<>();
    for (Worker w : employedWorkers) {
      if (!availableWorkers.contains(w))
        unavailable.add(w);
    }
    return unavailable;
  }

  public Set<Worker> getAssignedWorkers() {
    return assignedWorkers;
  }

  public Set<Worker> getUnassignedWorkers() {
    Set<Worker> unassigned = new HashSet<>();
    for (Worker w : employedWorkers) {
      if (!assignedWorkers.contains(w))
        unassigned.add(w);
    }
    return unassigned;
  }

  public Set<Project> getProjects() {
    return projects;
  }

  public Set<Qualification> getQualifications() {
    return qualifications;
  }

  public Worker createWorker(String name, Set<Qualification> qualifications, double salary) {
    try {
      if (name == null || name.isEmpty())
        throw new IllegalArgumentException();
      if (qualifications.isEmpty() || !this.qualifications.containsAll(qualifications))
        throw new IllegalArgumentException();
      Worker w = new Worker(name, qualifications, salary);
      for (Qualification qual : qualifications) {
        qual.addWorker(w);
      }
      availableWorkers.add(w);
      employedWorkers.add(w);
      return w;
    } catch (Exception e) {
      return null;
    }
  }

  public Qualification createQualification(String description) {
    if (description == null || description.isEmpty())
      return null;
    Qualification q = new Qualification(description);
    qualifications.add(q);
    return q;
  }

  public Project createProject(String n, Set<Qualification> qs, ProjectSize s) {
    try {
      if (n == null || n.isEmpty())
        throw new IllegalArgumentException();
      Project p = new Project(n, qs, s);
      projects.add(p);
      return p;
    } catch (Exception e) {
      return null;
    }
  }

  public void start(Project p) {
    if (p == null)
      throw new NullPointerException();
    if (p.getMissingQualifications().isEmpty())
      p.setStatus(ProjectStatus.ACTIVE);
  }

  public void finish(Project p) {
    if (p == null)
      throw new NullPointerException();
    if (p.getStatus() != ProjectStatus.ACTIVE)
      return;
    p.setStatus(ProjectStatus.FINISHED);
    Set<Worker> pWorkers = new HashSet<>(p.getWorkers());
    for (Worker w : pWorkers) {
      w.removeProject(p);
    }
    p.removeAllWorkers();
  }

  public void assign(Worker w, Project p) {
    if (w == null || p == null)
      throw new NullPointerException("Worker and Project must not be null");
    if (!availableWorkers.contains(w))
      throw new IllegalArgumentException("Worker is unavailable");
    if (p.getWorkers().contains(w))
      throw new IllegalArgumentException("Worker is already assigned to this project.");
    if (p.getStatus() == ProjectStatus.ACTIVE || p.getStatus() == ProjectStatus.FINISHED)
      throw new IllegalArgumentException("Project cannot be Active or Finished");
    if (w.willOverload(p))
      throw new IllegalArgumentException("Worker cannot get overloaded");
    if (!p.isHelpful(w))
      throw new IllegalArgumentException("Worker is not helpful to the project");
    p.addWorker(w);
    w.addProject(p);
    assignedWorkers.add(w);
    if (!w.isAvailable())
      availableWorkers.remove(w);
  }

  public void unassign(Worker w, Project p) {
    if (w == null || p == null)
      throw new NullPointerException();
    if (!p.getWorkers().contains(w))
      throw new IllegalArgumentException("Worker is not assigned to this project");
    w.removeProject(p);
    p.removeWorker(w);
    if (w.getProjects().isEmpty())
      assignedWorkers.remove(w);
    if (w.isAvailable() && !availableWorkers.contains(w))
      availableWorkers.add(w);
    if (!p.getMissingQualifications().isEmpty() && p.getStatus() == ProjectStatus.ACTIVE)
      p.setStatus(ProjectStatus.SUSPENDED);
  }

  public void unassignAll(Worker w) {
    if (w == null)
      throw new NullPointerException("Cannot unassign null worker");
    Set<Project> workerProjects = new HashSet<>(w.getProjects());
    for (Project p : workerProjects) {
      p.removeWorker(w);
      w.removeProject(p);
    }
  }

}
