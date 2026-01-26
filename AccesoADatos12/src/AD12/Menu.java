package AD12;

import java.sql.*;
import java.util.*;
import java.io.*;
import org.json.*;

public class Menu {

	private static final Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {
		try (Connection con = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/Alumnos01?useSSL=false&serverTimezone=UTC", "ADManager", "manager")) {

			int opcion;
			do {
				mostrarMenu();
				opcion = sc.nextInt();
				sc.nextLine();
				switch (opcion) {
				case 1: {
					insertarGrupo(con);
					break;
				}
				case 2: {
					insertarAlumno(con);
					break;
				}
				case 3: {
					mostrarAlumnos(con);
					break;
				}
				case 4: {
					exportarAlumnosTXT(con);
					break;
				}
				case 5: {
					importarAlumnosTXT(con);
					break;
				}
				case 6: {
					modificarNombreAlumno(con);
					break;
				}
				case 7: {
					eliminarAlumnoPK(con);
					break;
				}
				case 8: {
					eliminarAlumnosPorCurso(con);
					break;
				}
				case 9: {
					exportarGruposJSON(con);
					break;
				}
				case 10: {
					importarGruposJSON(con);
					break;
				}
				case 0: {
					System.out.println("Adiós");
					break;
				}
				default: System.out.println("Opción inválida");
				}
			} while (opcion != 0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void mostrarMenu() {
		System.out.println("\nMENU");
		System.out.println("1. Insertar grupo");
		System.out.println("2. Insertar alumno");
		System.out.println("3. Mostrar alumnos con su grupo");
		System.out.println("4. Exportar alumnos a TXT");
		System.out.println("5. Importar alumnos desde TXT");
		System.out.println("6. Modificar nombre alumno por PK");
		System.out.println("7. Eliminar alumno por PK");
		System.out.println("8. Eliminar alumnos por curso");
		System.out.println("9. Exportar grupos a JSON");
		System.out.println("10. Importar grupos desde JSON");
		System.out.println("0. Salir");
	}
	
	// Grupos
	private static void insertarGrupo(Connection con) throws SQLException {
		System.out.print("Nombre del grupo: ");
		String nombre = sc.nextLine();
		System.out.print("Ciclo: ");
		String ciclo = sc.nextLine();
		System.out.print("Curso: ");
		String curso = sc.nextLine();

		PreparedStatement ps = con.prepareStatement("INSERT INTO grupos (Nombre, Ciclo, Curso) VALUES (?, ?, ?)");
		ps.setString(1, nombre);
		ps.setString(3, ciclo);
		ps.setString(2, curso);
		ps.executeUpdate();

		System.out.println("Grupo insertado correctamente");
	}
	
	// Alumnos
	private static void insertarAlumno(Connection con) throws SQLException {
		mostrarGrupos(con);
		System.out.print("ID del grupo: ");
		int idGrupo = sc.nextInt();
		sc.nextLine();

		System.out.print("NIA: ");
		int nia = sc.nextInt();
		sc.nextLine();
		System.out.print("Nombre: ");
		String nombre = sc.nextLine();
		System.out.print("Apellidos: ");
		String apellidos = sc.nextLine();
		System.out.print("Género (M/F): ");
		char genero = sc.nextLine().charAt(0);
		System.out.print("Fecha nacimiento (YYYY-MM-DD): ");
		String fecha = sc.nextLine();

		PreparedStatement ps = con.prepareStatement("INSERT INTO alumno (Nia, Nombre, Apellidos, Genero, FechaNac, Grupo)"
				+ " VALUES (?, ?, ?, ?, ?, ?)");
		ps.setInt(1, nia);
		ps.setString(2, nombre);
		ps.setString(3, apellidos);
		ps.setString(4, String.valueOf(genero));
		ps.setString(5, fecha);
		ps.setInt(6, idGrupo);
		ps.executeUpdate();

		System.out.println("Alumno insertado correctamente");
	}

	private static void mostrarAlumnos(Connection con) throws SQLException {
		String sql = "SELECT a.Nia, a.Nombre, a.Apellidos, a.Genero, a.FechaNac, g.Nombre, g.Curso, g.Ciclo "
				+ "FROM alumnos a JOIN grupos g ON a.Id_grupo = g.Id_grupo";
		
		ResultSet rs = con.createStatement().executeQuery(sql);
		while (rs.next()) {
			System.out.printf("%d %s %s %s %s | Grupo: %s (%s - %s)%n", rs.getInt(1), rs.getString(2), rs.getString(3),
					rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8));
		}
	}
	
	private static void modificarNombreAlumno(Connection con) throws SQLException {
		System.out.println("Indique el NIA del alumno a modificar");
		int nia = sc.nextInt();
		System.out.println("Indique el nuevo nombre de la persona");
		String nombreNuevo = sc.nextLine();

		PreparedStatement ps = con.prepareStatement("UPDATE alumnos SET Nombre = ? WHERE Nia = ?");
		ps.setString(1, nombreNuevo);
		ps.setInt(2, nia);
		ps.executeUpdate();
	}

	private static void eliminarAlumnoPK(Connection con) throws SQLException {
		System.out.print("NIA del alumno: ");
		int nia = sc.nextInt();
		sc.nextLine();

		PreparedStatement ps = con.prepareStatement("DELETE FROM alumnos WHERE Nia = ?");
		ps.setInt(1, nia);
		ps.executeUpdate();
	}

	private static void eliminarAlumnosPorCurso(Connection con) throws SQLException {
		ResultSet rs = con.createStatement().executeQuery("SELECT Nombre, Curso, Ciclo FROM grupos");
		System.out.println("Cursos existentes:");
		while (rs.next()) {
			System.out.println("- " + rs.getString(1));
		}

		System.out.print("Curso a eliminar: ");
		String curso = sc.nextLine();

		PreparedStatement ps = con
				.prepareStatement("DELETE a FROM alumnos a JOIN grupos g ON a.Id_grupo = g.Id_grupo WHERE g.curso = ?");
		ps.setString(1, curso);
		ps.executeUpdate();
	}

	//Txt

	private static void exportarAlumnosTXT(Connection con) {
		System.out.print("Ruta TXT: ");
		String ruta = sc.nextLine();
		
		try (PrintWriter pw = new PrintWriter(new FileWriter(ruta));){
			ResultSet rs = con.createStatement().executeQuery("SELECT Nombre, Apellidos, Genero, FechaNAc, Id_grupo FROM alumno");
			while (rs.next()) {
				pw.printf("%d/%s/%s/%s/%s/%d%n", rs.getInt("Nia"), rs.getString("Nombre"), rs.getString("Apellidos"),
						rs.getString("Genero"), rs.getString("FechaNac"), rs.getInt("Id_grupo"));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}

	private static void importarAlumnosTXT(Connection con) throws Exception {
		System.out.print("Ruta TXT: ");
		BufferedReader br = new BufferedReader(new FileReader(sc.nextLine()));
		String linea;

		while ((linea = br.readLine()) != null) {
			String[] d = linea.split("/");
			PreparedStatement ps = con.prepareStatement("INSERT INTO alumnos VALUES (?, ?, ?, ?, ?, ?)");
			ps.setInt(1, Integer.parseInt(d[0]));
			ps.setString(2, d[1]);
			ps.setString(3, d[2]);
			ps.setString(4, d[3]);
			ps.setString(5, d[4]);
			ps.setInt(6, Integer.parseInt(d[5]));
			ps.executeUpdate();
		}
		br.close();
	}
	
	
	// JSon
	private static void exportarGruposJSON(Connection con) throws Exception {
	    JSONArray gruposArr = new JSONArray();

	    String sqlGrupos = "SELECT id_grupo, nombre, curso, ciclo FROM grupos";
	    ResultSet rsGrupos = con.createStatement().executeQuery(sqlGrupos);

	    while (rsGrupos.next()) {
	        JSONObject grupoJSON = new JSONObject();
	        int idGrupo = rsGrupos.getInt("id_grupo");

	        grupoJSON.put("id_grupo", idGrupo);
	        grupoJSON.put("nombre", rsGrupos.getString("nombre"));
	        grupoJSON.put("curso", rsGrupos.getString("curso"));
	        grupoJSON.put("ciclo", rsGrupos.getString("ciclo"));

	        JSONArray alumnosArr = new JSONArray();
	        PreparedStatement psAlu = con.prepareStatement(
	                "SELECT Nia, Nombre, Apellidos, Genero, FechaNac FROM alumnos WHERE id_grupo = ?");
	        psAlu.setInt(1, idGrupo);
	        ResultSet rsAlu = psAlu.executeQuery();

	        while (rsAlu.next()) {
	            JSONObject aluJSON = new JSONObject();
	            aluJSON.put("nia", rsAlu.getInt("Nia"));
	            aluJSON.put("nombre", rsAlu.getString("Nombre"));
	            aluJSON.put("apellidos", rsAlu.getString("Apellidos"));
	            aluJSON.put("genero", rsAlu.getString("Genero"));
	            aluJSON.put("fechaNac", rsAlu.getString("FechaNac"));
	            alumnosArr.put(aluJSON);
	        }

	        grupoJSON.put("alumnos", alumnosArr);
	        gruposArr.put(grupoJSON);
	    }

	    try (FileWriter fw = new FileWriter("grupos.json")) {
	        fw.write(gruposArr.toString(2));
	    }

	    System.out.println("Grupos exportados a JSON correctamente");
	}

	// Importa grupos y alumnos desde JSON usando BufferedReader
	private static void importarGruposJSON(Connection con) throws Exception {
	    System.out.print("Ruta del JSON: ");
	    String ruta = sc.nextLine();

	    StringBuilder sb = new StringBuilder();
	    try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
	        String linea;
	        while ((linea = br.readLine()) != null) {
	            sb.append(linea);
	        }
	    }
	    String contenido = sb.toString();

	    JSONArray gruposArr = new JSONArray(contenido);

	    for (int i = 0; i < gruposArr.length(); i++) {
	        JSONObject grupoJSON = gruposArr.getJSONObject(i);

	        PreparedStatement psGrupo = con.prepareStatement(
	                "INSERT INTO grupos (nombre, curso, ciclo) VALUES (?, ?, ?)",
	                Statement.RETURN_GENERATED_KEYS);
	        psGrupo.setString(1, grupoJSON.getString("nombre"));
	        psGrupo.setString(2, grupoJSON.getString("curso"));
	        psGrupo.setString(3, grupoJSON.getString("ciclo"));
	        psGrupo.executeUpdate();

	        ResultSet keys = psGrupo.getGeneratedKeys();
	        keys.next();
	        int idGrupo = keys.getInt(1);

	        JSONArray alumnosArr = grupoJSON.getJSONArray("alumnos");
	        for (int j = 0; j < alumnosArr.length(); j++) {
	            JSONObject aluJSON = alumnosArr.getJSONObject(j);

	            PreparedStatement psAlu = con.prepareStatement(
	                    "INSERT INTO alumnos (Nia, Nombre, Apellidos, Genero, FechaNac, id_grupo) VALUES (?, ?, ?, ?, ?, ?)");
	            psAlu.setInt(1, aluJSON.getInt("nia"));
	            psAlu.setString(2, aluJSON.getString("nombre"));
	            psAlu.setString(3, aluJSON.getString("apellidos"));
	            psAlu.setString(4, aluJSON.getString("genero"));
	            psAlu.setString(5, aluJSON.getString("fechaNac"));
	            psAlu.setInt(6, idGrupo);
	            psAlu.executeUpdate();
	        }
	    }

	    System.out.println("Grupos importados desde JSON correctamente");
	}

	private static void mostrarGrupos(Connection con) throws SQLException {
	    ResultSet rs = con.createStatement().executeQuery("SELECT * FROM grupos");
	    System.out.println("Grupos disponibles:");
	    while (rs.next()) {
	        System.out.printf("%d - %s (%s - %s)%n",
	                rs.getInt("id_grupo"), rs.getString("nombre"),
	                rs.getString("curso"), rs.getString("ciclo"));
	    }
	}
}
