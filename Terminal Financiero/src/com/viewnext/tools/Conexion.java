package com.viewnext.tools;

import com.viewnext.configuration.models.UserProperties;

import com.viewnext.configuration.panels.ConfigPanel;
import com.viewnext.database.models.Centro;

import java.awt.HeadlessException;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import java.sql.Timestamp;


public class Conexion {
	private Connection conn;
	private String connectionName;
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> cierreInactividad;
	private static final int INACTIVIDAD_SEGUNDOS = 10;
	public StringBuilder sql;
	public String sqlFinal;
	
	
	public Conexion() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			this.cerrarConexion();
			this.scheduler.shutdown();
		}));
	}

	private synchronized void abrirConexion() {
	    try {
	        if (this.conn != null && !this.conn.isClosed()) {
	            System.out.println("Conexión ya abierta: " + this.connectionName);
	            this.reiniciarTemporizadorCierre();
	        } else {
	            UserProperties user = XmlBuilder.leerXML(obtenerUltimoUsuario());

	            if (user != null &&
	                user.getIpServer() != null &&
	                user.getPort() != 0 &&
	                user.getInstanceBBDD() != null &&
	                user.getUserName() != null &&
	                user.getUserPassword() != null) {

	                String ip = "localhost";
	                String puerto = String.valueOf(user.getPort());
	                String serviceName = "ORCLCDB"; // Debe ser "ORCLCDB"
	                String usuario = user.getUserName();
	                String contraseña = user.getUserPassword();

	                this.connectionName = ip + ":" + puerto + "/" + serviceName;

	                String url = "jdbc:oracle:thin:@" + this.connectionName;
	                System.out.println("Probando conexión con: " + url + " usuario: " + usuario);

	                Class.forName("oracle.jdbc.driver.OracleDriver"); // solo si quieres forzar el cargador

	                this.conn = DriverManager.getConnection(url, usuario, contraseña);
	                System.out.println("Conexión abierta: " + this.connectionName);

	                this.reiniciarTemporizadorCierre();
	            } else {
	                JOptionPane.showMessageDialog(null, " Configuración incompleta. Verifica el archivo XML.", "Error", JOptionPane.ERROR_MESSAGE);
	                Logger.logError("Configuración incompleta", new IllegalStateException("Faltan datos en XML"));
	                throw new IllegalStateException("Faltan datos obligatorios en el archivo XML.");
	            }
	        }
	    } catch (HeadlessException | SQLException | ClassNotFoundException e) {
	        JOptionPane.showMessageDialog(null, "Error de conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        Logger.logError("Error de conexión Oracle", e);
	        throw new IllegalStateException("No se pudo conectar a Oracle", e);
	    }
	}


	private synchronized void reiniciarTemporizadorCierre() {
		if (this.cierreInactividad != null && !this.cierreInactividad.isDone()) {
			this.cierreInactividad.cancel(false);
		}

		this.cierreInactividad = this.scheduler.schedule(this::cerrarConexion, INACTIVIDAD_SEGUNDOS, TimeUnit.SECONDS);
	}

	public synchronized void cerrarConexion() {
		if (this.conn != null) {
			try {
				if (!this.conn.isClosed()) {
					this.conn.close();
					System.out.println("Conexión cerrada por inactividad o fin de aplicación: " + this.connectionName);
				}
			} catch (SQLException e) {
				Logger.logError("Error al cerrar la conexión", e);
				e.printStackTrace();
			} finally {
				this.conn = null;
			}
		}

	}

	public static String obtenerUltimoUsuario() {
		Properties props = new Properties();
		try (FileInputStream fis = new FileInputStream("config.properties")) {
			props.load(fis);
			return props.getProperty("ultimo_usuario");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public synchronized List<Centro> buscarCentros(String empresa, String centro, String terminal, String version) throws SQLException {
	    this.abrirConexion();
	    List<Centro> resultados = new ArrayList<>();
 
	    this.sql = new StringBuilder("SELECT EMPRESA, CENTROCAJA, TERMINAL, VERSIONTF, IPTERMINAL, NAMEMAQUINA, FECHAHORA FROM T2223300 WHERE 1=1");
 
	    if (!empresa.isEmpty()) {
	        sql.append(" AND EMPRESA = ?");
	    }
 
	    if (!centro.isEmpty()) {
	        sql.append(" AND CENTROCAJA = ?");
	    }
 
	    if (!terminal.isEmpty()) {
	        sql.append(" AND TERMINAL = ?");
	    }
 
	    if (!version.isEmpty()) {
	        sql.append(" AND VERSIONTF = ?");
	    }
 
	    sql.append(" ORDER BY EMPRESA, CENTROCAJA, TERMINAL, VERSIONTF DESC");
 
	    try (PreparedStatement ps = this.conn.prepareStatement(sql.toString())) {
	        ps.setQueryTimeout(4);
	        int idx = 1;
 
	        List<Object> parametros = new ArrayList<>();
 
	        if (!empresa.isEmpty()) {
	            int valor = Integer.parseInt(empresa);
	            ps.setInt(idx++, valor);
	            parametros.add(valor);
	        }
 
	        if (!centro.isEmpty()) {
	            int valor = Integer.parseInt(centro);
	            ps.setInt(idx++, valor);
	            parametros.add(valor);
	        }
 
	        if (!terminal.isEmpty()) {
	            int valor = Integer.parseInt(terminal);
	            ps.setInt(idx++, valor);
	            parametros.add(valor);
	        }
 
	        if (!version.isEmpty()) {
	            ps.setString(idx++, version);
	            parametros.add(version);
	        }
 
	        // Mostrar SQL final con valores sustituidos
	        sqlFinal = sql.toString();
	        for (Object param : parametros) {
	            String valorFormateado = (param instanceof String) ? "'" + param + "'" : param.toString();
	            sqlFinal = sqlFinal.replaceFirst("\\?", valorFormateado);
	        }
	        System.out.println("Consulta SQL generada: " + sqlFinal);
	        
 
	        try (ResultSet rs = ps.executeQuery()) {
	            ResultSetMetaData metaData = rs.getMetaData();
	            int columnCount = metaData.getColumnCount();
	            List<String> columnasPresentes = new ArrayList<>();
 
	            for (int i = 1; i <= columnCount; i++) {
	                columnasPresentes.add(metaData.getColumnLabel(i).toUpperCase());
	            }
 
	            StringBuilder errores = new StringBuilder();
 
	            if (!columnasPresentes.contains("EMPRESA")) errores.append("Columna EMPRESA no encontrada. ");
	            if (!columnasPresentes.contains("CENTROCAJA")) errores.append("Columna CENTROCAJA no encontrada. ");
	            if (!columnasPresentes.contains("TERMINAL")) errores.append("Columna TERMINAL no encontrada. ");
	            if (!columnasPresentes.contains("VERSIONTF")) errores.append("Columna VERSIONTF no encontrada. ");
	            if (!columnasPresentes.contains("IPTERMINAL")) errores.append("Columna IPTERMINAL no encontrada. ");
	            if (!columnasPresentes.contains("NAMEMAQUINA")) errores.append("Columna NAMEMAQUINA no encontrada. ");
	            if (!columnasPresentes.contains("FECHAHORA")) errores.append("Columna FECHAHORA no encontrada. ");
 
	            if (errores.length() > 0) {
	                String mensajeError = "Error en la estructura de la tabla: " + errores.toString();
	                Logger.logError(mensajeError, new IllegalStateException(mensajeError));
	                JOptionPane.showMessageDialog(null, mensajeError, "Error", JOptionPane.ERROR_MESSAGE);
	            }
 
	            while (rs.next()) {
	                int empresaComprobacion = columnasPresentes.contains("EMPRESA") ? rs.getInt("EMPRESA") : 0;
	                int centroComprobacion = columnasPresentes.contains("CENTROCAJA") ? rs.getInt("CENTROCAJA") : 0;
	                int terminalComprobacion = columnasPresentes.contains("TERMINAL") ? rs.getInt("TERMINAL") : 0;
	                String versionTF = columnasPresentes.contains("VERSIONTF") ? rs.getString("VERSIONTF") : null;
	                String ipTerminal = columnasPresentes.contains("IPTERMINAL") ? rs.getString("IPTERMINAL") : null;
	                String nameMaquina = columnasPresentes.contains("NAMEMAQUINA") ? rs.getString("NAMEMAQUINA") : null;
	                Timestamp fechaHora = columnasPresentes.contains("FECHAHORA") ? rs.getTimestamp("FECHAHORA") : null;
 
	                resultados.add(new Centro(empresaComprobacion, centroComprobacion, terminalComprobacion, versionTF, ipTerminal, nameMaquina, fechaHora));
	            }
	        }
 
		} catch (SQLTimeoutException e) {
			Logger.logError("Timeout superado" , e);
			JOptionPane.showInputDialog("Tiempo de espera superado. La consulta tardó más de 4 segundos.");
			throw new SQLException("Tiempo de espera superado. La consulta tardó más de 4 segundos.", e);
		} catch (SQLException e) {
			Logger.logError("Error al ejecutar la consulta", e);
			int codigoError = e.getErrorCode();
 
			switch (codigoError) {
				case 942: // ORA-00942
					JOptionPane.showMessageDialog(null, "Error: La tabla especificada no existe en la base de datos (ORA-00942).", "Error", JOptionPane.ERROR_MESSAGE);
					Logger.logError(  "Error: La tabla especificada no existe en la base de datos (ORA-00942)." , e );
					throw new SQLException("Error: La tabla especificada no existe en la base de datos (ORA-00942).", e);
 
				case 904: // ORA-00904
					JOptionPane.showMessageDialog(null, "Error: Una de las columnas especificadas no es válida (ORA-00904).", "Error", JOptionPane.ERROR_MESSAGE);
					Logger.logError(  "Error: Una de las columnas especificadas no es válida (ORA-00904)." , e );
					throw new SQLException("Error: Una de las columnas especificadas no es válida (ORA-00904).", e);
 
				case 1017: // ORA-01017
					JOptionPane.showMessageDialog(null, "Error: Usuario o contraseña inválidos (ORA-01017).", "Error", JOptionPane.ERROR_MESSAGE);
					Logger.logError(  "Error: Usuario o contraseña inválidos (ORA-01017)." , e );
					throw new SQLException("Error: Usuario o contraseña inválidos (ORA-01017).", e);
 
				case 933: // ORA-00933
					JOptionPane.showMessageDialog(null, "Error de sintaxis en la consulta SQL (ORA-00933).", "Error", JOptionPane.ERROR_MESSAGE);
					Logger.logError(  "Error de sintaxis en la consulta SQL (ORA-00933)." , e );
					throw new SQLException("Error de sintaxis en la consulta SQL (ORA-00933).", e);
 
				default:
					JOptionPane.showMessageDialog(null, "Error al ejecutar la consulta en la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					Logger.logError(  "Error al ejecutar la consulta en la base de datos: " , e );
					throw new SQLException("Error al ejecutar la consulta en la base de datos: " + e.getMessage(), e);
			}
		}

		catch (Exception e){
			Logger.logError("Se ha producido un error desconocido" , e);
			JOptionPane.showMessageDialog(null , "Error desconocido");
			throw new RuntimeException("Error desconocido",e);
		}

		this.reiniciarTemporizadorCierre();
		return resultados;
	}
}