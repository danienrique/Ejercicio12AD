package AD12;

import java.sql.*;
import java.sql.Date;
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
				opcion = Integer.parseInt(sc.nextLine());
				switch (opcion) {
				case 1 -> insertarGrupo(con);
				case 2 -> insertarAlumno(con);
				case 3 -> mostrarAlumnos(con);
				case 4 -> exportarAlumnosTXT(con);
				case 5 -> importarAlumnosTXT(con);
				case 6 -> modificarNombreAlumno(con);
				case 7 -> eliminarAlumnoPK(con);
				case 8 -> eliminarAlumnosPorCurso(con);
				case 9 -> exportarGruposJSON(con);
				case 10 -> importarGruposJSON(con);
				case 0 -> System.out.println("Adiós");
				default -> System.out.println("Opción inválida");
				}
			} while (opcion != 0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ================= MENU =================
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

	//Grupos
	private static void insertarGrupo(Connection con) throws SQLException {
		System.out.print("Nombre del grupo: ");
		String nombre = sc.nextLine();
		System.out.print("Ciclo: ");
		String ciclo = sc.nextLine();
		System.out.print("Curso: ");
		String curso = sc.nextLine();

		String sql = "INSERT INTO grupos (Nombre, Ciclo, Curso) VALUES (?, ?, ?)";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, nombre);
			ps.setString(2, ciclo);
			ps.setString(3, curso);
			ps.executeUpdate();
		}
		System.out.println("Grupo insertado correctamente");
	}

	private static void mostrarGrupos(Connection con) throws SQLException {
		ResultSet rs = con.createStatement().executeQuery("SELECT Id_grupo, Nombre, Ciclo, Curso FROM grupos");
		while (rs.next()) {
			System.out.printf("%d - %s (%s - %s)%n", rs.getInt("Id_grupo"), rs.getString("Nombre"),
					rs.getString("Ciclo"), rs.getString("Curso"));
		}
	}

	//Alumnos
	private static void insertarAlumno(Connection con) throws SQLException {
		mostrarGrupos(con);
		System.out.print("ID del grupo: ");
		int idGrupo = Integer.parseInt(sc.nextLine());

		System.out.print("NIA: ");
		int nia = Integer.parseInt(sc.nextLine());
		System.out.print("Nombre: ");
		String nombre = sc.nextLine();
		System.out.print("Apellidos: ");
		String apellidos = sc.nextLine();
		System.out.print("Género (M/F): ");
		String genero = sc.nextLine();
		System.out.print("Fecha nacimiento (YYYY-MM-DD): ");
		String fecha = sc.nextLine();

		String sql = "INSERT INTO alumnos (Nia, Nombre, Apellidos, Genero, Fecha_Nac, Id_grupo) VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, nia);
			ps.setString(2, nombre);
			ps.setString(3, apellidos);
			ps.setString(4, genero);
			ps.setDate(5, Date.valueOf(fecha));
			ps.setInt(6, idGrupo);
			ps.executeUpdate();
		}
		System.out.println("Alumno insertado correctamente");
	}

	private static void mostrarAlumnos(Connection con) throws SQLException {
		String sql = "SELECT a.Nia, a.Nombre, a.Apellidos, a.Genero, a.Fecha_Nac, "
				+ "g.Nombre AS Grupo, g.Ciclo, g.Curso " + "FROM alumnos a JOIN grupos g ON a.Id_grupo = g.Id_grupo";

		try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) {
				System.out.printf("%d %s %s %s %s | Grupo: %s (%s - %s)%n", rs.getInt("Nia"), rs.getString("Nombre"),
						rs.getString("Apellidos"), rs.getString("Genero"), rs.getDate("Fecha_Nac"),
						rs.getString("Grupo"), rs.getString("Ciclo"), rs.getString("Curso"));
			}
		}
	}

	private static void modificarNombreAlumno(Connection con) throws SQLException {
		System.out.print("NIA del alumno: ");
		int nia = Integer.parseInt(sc.nextLine());
		System.out.print("Nuevo nombre: ");
		String nombreNuevo = sc.nextLine();

		String sql = "UPDATE alumnos SET Nombre = ? WHERE Nia = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, nombreNuevo);
			ps.setInt(2, nia);
			ps.executeUpdate();
		}
	}

	private static void eliminarAlumnoPK(Connection con) throws SQLException {
		System.out.print("NIA del alumno: ");
		int nia = Integer.parseInt(sc.nextLine());

		try (PreparedStatement ps = con.prepareStatement("DELETE FROM alumnos WHERE Nia = ?")) {
			ps.setInt(1, nia);
			ps.executeUpdate();
		}
	}

	private static void eliminarAlumnosPorCurso(Connection con) throws SQLException {
		System.out.print("Curso a eliminar: ");
		String curso = sc.nextLine();

		String sql = "DELETE a FROM alumnos a JOIN grupos g ON a.Id_grupo = g.Id_grupo WHERE g.Curso = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, curso);
			ps.executeUpdate();
		}
	}

	// Txt
	private static void exportarAlumnosTXT(Connection con) {
		System.out.print("Ruta TXT: ");
		String ruta = sc.nextLine();

		String sql = "SELECT Nia, Nombre, Apellidos, Genero, Fecha_Nac, Id_grupo FROM alumnos";
		try (PrintWriter pw = new PrintWriter(new FileWriter(ruta));
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(sql)) {

			while (rs.next()) {
				pw.printf("%d/%s/%s/%s/%s/%d%n", rs.getInt("Nia"), rs.getString("Nombre"), rs.getString("Apellidos"),
						rs.getString("Genero"), rs.getDate("Fecha_Nac"), rs.getInt("Id_grupo"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void importarAlumnosTXT(Connection con) throws Exception {
		System.out.print("Ruta TXT: ");
		String ruta = sc.nextLine();

		try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
			String linea;
			String sql = "INSERT INTO alumnos (Nia, Nombre, Apellidos, Genero, Fecha_Nac, Id_grupo) VALUES (?, ?, ?, ?, ?, ?)";

			while ((linea = br.readLine()) != null) {
				String[] d = linea.split("/");
				try (PreparedStatement ps = con.prepareStatement(sql)) {
					ps.setInt(1, Integer.parseInt(d[0]));
					ps.setString(2, d[1]);
					ps.setString(3, d[2]);
					ps.setString(4, d[3]);
					ps.setDate(5, Date.valueOf(d[4]));
					ps.setInt(6, Integer.parseInt(d[5]));
					ps.executeUpdate();
				}
			}
		}
	}

	//JSon-
	private static void exportarGruposJSON(Connection con) throws Exception {
		JSONArray gruposArr = new JSONArray();

		String sqlGrupos = "SELECT Id_grupo, Nombre, Ciclo, Curso FROM grupos";
		try (Statement st = con.createStatement(); ResultSet rsGrupos = st.executeQuery(sqlGrupos)) {

			while (rsGrupos.next()) {
				JSONObject grupoJSON = new JSONObject();
				int id = rsGrupos.getInt("Id_grupo");

				grupoJSON.put("id_grupo", id);
				grupoJSON.put("nombre", rsGrupos.getString("Nombre"));
				grupoJSON.put("ciclo", rsGrupos.getString("Ciclo"));
				grupoJSON.put("curso", rsGrupos.getString("Curso"));

				JSONArray alumnosArr = new JSONArray();
				String sqlAlu = "SELECT Nia, Nombre, Apellidos, Genero, Fecha_Nac FROM alumnos WHERE Id_grupo = ?";
				try (PreparedStatement ps = con.prepareStatement(sqlAlu)) {
					ps.setInt(1, id);
					try (ResultSet rsAlu = ps.executeQuery()) {
						while (rsAlu.next()) {
							JSONObject aluJSON = new JSONObject();
							aluJSON.put("nia", rsAlu.getInt("Nia"));
							aluJSON.put("nombre", rsAlu.getString("Nombre"));
							aluJSON.put("apellidos", rsAlu.getString("Apellidos"));
							aluJSON.put("genero", rsAlu.getString("Genero"));
							aluJSON.put("fechaNac", rsAlu.getDate("Fecha_Nac").toString());
							alumnosArr.put(aluJSON);
						}
					}
				}

				grupoJSON.put("alumnos", alumnosArr);
				gruposArr.put(grupoJSON);
			}
		}

		try (FileWriter fw = new FileWriter("grupos.json")) {
			fw.write(gruposArr.toString(2));
		}
	}

	private static void importarGruposJSON(Connection con) throws Exception {
		System.out.print("Ruta JSON: ");
		String ruta = sc.nextLine();

		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
			String linea;
			while ((linea = br.readLine()) != null)
				sb.append(linea);
		}

		JSONArray gruposArr = new JSONArray(sb.toString());

		for (int i = 0; i < gruposArr.length(); i++) {
			JSONObject g = gruposArr.getJSONObject(i);

			String sqlGrupo = "INSERT INTO grupos (Nombre, Ciclo, Curso) VALUES (?, ?, ?)";
			int newId;
			try (PreparedStatement psG = con.prepareStatement(sqlGrupo, Statement.RETURN_GENERATED_KEYS)) {
				psG.setString(1, g.getString("nombre"));
				psG.setString(2, g.getString("ciclo"));
				psG.setString(3, g.getString("curso"));
				psG.executeUpdate();

				try (ResultSet rs = psG.getGeneratedKeys()) {
					rs.next();
					newId = rs.getInt(1);
				}
			}

			JSONArray alumnos = g.getJSONArray("alumnos");
			String sqlAlu = "INSERT INTO alumnos (Nia, Nombre, Apellidos, Genero, Fecha_Nac, Id_grupo) VALUES (?, ?, ?, ?, ?, ?)";

			for (int j = 0; j < alumnos.length(); j++) {
				JSONObject a = alumnos.getJSONObject(j);
				try (PreparedStatement psA = con.prepareStatement(sqlAlu)) {
					psA.setInt(1, a.getInt("nia"));
					psA.setString(2, a.getString("nombre"));
					psA.setString(3, a.getString("apellidos"));
					psA.setString(4, a.getString("genero"));
					psA.setDate(5, Date.valueOf(a.getString("fechaNac")));
					psA.setInt(6, newId);
					psA.executeUpdate();
				}
			}
		}
	}
}
