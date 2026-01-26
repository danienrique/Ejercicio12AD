package AD12;

import java.time.LocalDate;

public class Alumno {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	private int nia;
	private String nombre, apellidos, ciclo, curso, grupo;
	private char genero;
	private LocalDate fecha_nacimiento;
	
	public int getNia() {
		return nia;
	}
	public void setNia(int nia) {
		this.nia = nia;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellidos() {
		return apellidos;
	}
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	public String getCiclo() {
		return ciclo;
	}
	public void setCiclo(String ciclo) {
		this.ciclo = ciclo;
	}
	public String getCurso() {
		return curso;
	}
	public void setCurso(String curso) {
		this.curso = curso;
	}
	public String getGrupo() {
		return grupo;
	}
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	public char getGenero() {
		return genero;
	}
	public void setGenero(char genero) {
		this.genero = genero;
	}
	public LocalDate getFecha_nacimiento() {
		return fecha_nacimiento;
	}
	public void setFecha_nacimiento(LocalDate fecha_nacimiento) {
		this.fecha_nacimiento = fecha_nacimiento;
	}
	Alumno(){
	}
	public Alumno(int ni, String name, String surname,char genre, int anio, int mes, int dia, String cicle, String curse, String group) {
		this.nia = ni;
		this.nombre = name;
		this.apellidos = surname;
		this.ciclo = cicle;
		this.curso = curse;
		this.grupo = group;
		comprobacionGenero(genre);
		convertidorDeFechas(anio,mes,dia);
	}
	private void comprobacionGenero(char gen) {
		String aux = gen + "";
		if(aux.equals("M")||aux.equals("F")||aux.equals("m")||aux.equals("f")) {
			this.genero = gen;
		} else{
			System.err.println("Genero No Válido");
			System.exit(0);
		}
		
	}
	
	void convertidorDeFechas(int year, int month, int day) {
		fecha_nacimiento=LocalDate.of(year, month, day);
	}

	public String fechaNacimientoSQL() {
		return this.getFecha_nacimiento().getYear()+"-"+this.getFecha_nacimiento().getMonthValue()+"-"+this.getFecha_nacimiento().getDayOfMonth();
	}
}
