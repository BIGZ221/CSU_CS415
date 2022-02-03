package a1;

import java.util.HashSet;
import java.util.Set;

public class Worker {

  private String name;
  private double salary;
  private Set<Qualification> qualifications;
  private Set<Project> projects;
  private int workload;

  public Worker(String name, Set<Qualification> qs, double salary) {
    if (name == null || name.isEmpty())
      throw new IllegalArgumentException(String.format("name must not be empty or null. Was: '%s'", name));
    this.name = name;
    if (qs == null || qs.isEmpty())
      throw new IllegalArgumentException("Worker must have atleast one qualification.");
    qualifications = qs;
    this.salary = salary;
    this.projects = new HashSet<>();
    workload = 0;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Worker))
      return false;
    Worker rhs = (Worker) o;
    return name.equals(rhs.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return String.format("%s:%d:%d:%d", name, projects.size(), qualifications.size(), (int) salary);
  }

  public String getName() {
    return name;
  }

  public void setName(String s) {
    if (s == null || s.isEmpty())
      throw new IllegalArgumentException("New name must not be null or empty");
    name = s;
  }

  public double getSalary() {
    return salary;
  }

  public void setSalary(double salary) {
    if (salary < 0)
      throw new IllegalArgumentException("New salary must not be negative.");
    this.salary = salary;
  }

  public Set<Qualification> getQualifications() {
    return qualifications;
  }

  public void addQualification(Qualification q) {
    if (q == null)
      throw new NullPointerException();
    if (qualifications.contains(q))
      throw new IllegalArgumentException("New Qualification must be unique");
    qualifications.add(q);
  }

  public Set<Project> getProjects() {
    return projects;
  }

  private int getWorkloadAmount(ProjectSize size) {
    switch (size) {
      case SMALL:
        return 1;
      case MEDIUM:
        return 2;
      case BIG:
        return 3;
      default:
        throw new IllegalArgumentException("Unknown size attempted");
    }
  }

  public void addProject(Project p) {
    if (p == null)
      throw new NullPointerException("New Project must not be null");
    if (projects.contains(p))
      throw new IllegalArgumentException("New Project must be unique");
    workload += getWorkloadAmount(p.getSize());
    projects.add(p);
  }

  public void removeProject(Project p) {
    if (p == null)
      throw new NullPointerException("Cannot remove null Project");
    workload -= getWorkloadAmount(p.getSize());
    projects.remove(p);
  }

  public int getWorkload() {
    return workload;
  }

  public boolean willOverload(Project p) {
    if (p == null)
      throw new NullPointerException();
    if (projects.contains(p))
      throw new IllegalArgumentException("Project must be unique");
    int projectCost = getWorkloadAmount(p.getSize());
    return workload + projectCost > 12;
  }

  public boolean isAvailable() {
    return workload < 12;
  }
}
