package mz.co.crud.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(name = "years")
@Data
@NamedQueries({ @NamedQuery(name = "Year.findAll", query = "SELECT y FROM Year y ORDER BY y.id DESC"),
		@NamedQuery(name = "Year.findById", query = "SELECT y FROM Year y WHERE y.id = :id"),
		@NamedQuery(name = "Year.findByOpened", query = "SELECT y FROM Year y WHERE y.opened ='Sim'"),
		@NamedQuery(name = "Year.findByYear", query = "SELECT y FROM Year y WHERE y.year = :year") })
public class Year implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = true)
	private String description;

	@Column(nullable = false, unique = true)
	private int year;

	@Column(name = "opened", nullable = false, length = 3)
	private String opened;

	public Year() {
	}

	public Year(int year) {
		this.year = year;
		this.description = "Ano " + year;
		this.opened = "Sim";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getOpened() {
		return opened;
	}

	public void setOpened(String opened) {
		this.opened = opened;
	}

}
