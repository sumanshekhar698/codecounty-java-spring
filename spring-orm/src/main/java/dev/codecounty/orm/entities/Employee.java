package dev.codecounty.orm.entities;

import java.util.List;

import org.hibernate.annotations.BatchSize;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "employees")
public class Employee {
	@Id// TODO: How to set composite key in Hibernate
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String name;
	
	// Optimistic Locking: Hibernate manages this automatically
    @Version
    private Integer version;
	
	// LAZY loading triggers the N+1 problem when accessing the list in a loop
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@BatchSize(size = 3)
	private List<Project> projects;

	// Getters and Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}
	
	public Integer getVersion() { return version; }

	@Override
	public String toString() {
		return "Employee{" +
				"id=" + id +
				", name='" + name + '\'' +
				", version=" + version +
				", projects=" + projects +
				'}';
	}
}