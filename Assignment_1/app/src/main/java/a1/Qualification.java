package a1;

import java.util.HashSet;
import java.util.Set;

public class Qualification {

  private String description;
  private Set<Worker> workers;

  public Qualification(String s) {
    if (s == null || s.isEmpty())
      throw new IllegalArgumentException(
          String.format("description must not be empty or null. Was: '%s'", description));
    description = s;
    workers = new HashSet<>();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Qualification))
      return false;
    Qualification rhs = (Qualification) o;
    return description.equals(rhs.description);
  }

  @Override
  public int hashCode() {
    return description.hashCode();
  }

  @Override
  public String toString() {
    return description;
  }

  public Set<Worker> getWorkers() {
    return workers;
  }

  public void addWorker(Worker w) {
    if (w == null)
      throw new NullPointerException("Cannot add null worker.");
    workers.add(w);
  }

  public void removeWorker(Worker w) {
    if (w == null)
      throw new NullPointerException("Cannot remove null worker");
    workers.remove(w);
  }
}
