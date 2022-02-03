package a1;

import java.util.HashSet;
import java.util.Set;

public class Project {

  private String name;
  private ProjectSize size;
  private ProjectStatus status;
  private Set<Qualification> qualifications;
  private Set<Worker> workers;

  public Project(String name, Set<Qualification> qualifications, ProjectSize size) {
    if (name == null || name.isEmpty())
      throw new IllegalArgumentException("Name is null or empty");
    if (qualifications == null)
      throw new NullPointerException("Qualifications must not be null");
    if (qualifications.isEmpty())
      throw new IllegalArgumentException("Qualifications must not be empty");
    this.name = name;
    this.qualifications = qualifications;
    this.size = size;
    workers = new HashSet<>();
    status = ProjectStatus.PLANNED;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Project))
      return false;
    Project p = (Project) o;
    return name.equals(p.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return String.format("%s:%d:%s", name, workers.size(), status.toString());
  }

  public String getName() {
    return name;
  }

  public void setName(String s) {
    if (s == null || s.isEmpty())
      throw new IllegalArgumentException();
    name = s;
  }

  public ProjectSize getSize() {
    return size;
  }

  public void setSize(ProjectSize size) {
    if (size == null)
      throw new NullPointerException();
    this.size = size;
  }

  public ProjectStatus getStatus() {
    return status;
  }

  public void setStatus(ProjectStatus status) {
    if (status == null)
      throw new NullPointerException();
    this.status = status;
  }

  public void addWorker(Worker w) {
    if (w == null)
      throw new NullPointerException();
    if (workers.contains(w))
      throw new IllegalArgumentException();
    workers.add(w);
  }

  public void removeWorker(Worker w) {
    if (w == null)
      throw new NullPointerException();
    if (!workers.contains(w))
      throw new IllegalArgumentException();
    workers.remove(w);
  }

  public Set<Worker> getWorkers() {
    return workers;
  }

  public void removeAllWorkers() {
    workers.clear();
  }

  public Set<Qualification> getRequiredQualifications() {
    return qualifications;
  }

  public void addQualification(Qualification q) {
    if (q == null)
      throw new NullPointerException();
    if (qualifications.contains(q))
      throw new IllegalArgumentException();
    qualifications.add(q);
  }

  public Set<Qualification> getMissingQualifications() {
    Set<Qualification> missing = new HashSet<>(qualifications);
    for (Worker w : workers) {
      missing.removeAll(w.getQualifications());
    }
    return missing;
  }

  public boolean isHelpful(Worker w) {
    if (w == null)
      throw new NullPointerException();
    Set<Qualification> missing = getMissingQualifications();
    for (Qualification q : w.getQualifications()) {
      if (missing.contains(q))
        return true;
    }
    return false;
  }
}
