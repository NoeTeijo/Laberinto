package com.mycompany.laberinto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Kain
 */
public class Laberinto extends JPanel implements KeyListener {

    private int puntuacion = 0;
    private int[][] laberinto = {
        {1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0},
        {0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1},
        {1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 4},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 5}
    };


    private int posX = 0;
    private int posY = 0;
    private int posXenemigo = 6;
    private int posYenemigo = 15;
    private int posXenemigoAnterior;
    private int posYenemigoAnterior;
    private int nivelActual=0;
    private boolean todasRecogidas;
    private String nombreUsuario;
    private Clip clipFondo;
    private Clip clipSalida;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Laberinto");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Laberinto laberintoPanel = new Laberinto();
            frame.add(laberintoPanel);
            frame.setSize(815, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            // Cargar el icono desde un archivo 
            ImageIcon icono = new ImageIcon("C:\\Users\\noech\\Desktop\\Cosas Personales\\Arte personal\\OCs\\Tricksy\\Emotes\\Tricksy_sip by @Honey_llemon favicom.png");

            // Establecer el icono de la ventana
            frame.setIconImage(icono.getImage());
            

            JButton guardarPartidaButton = new JButton("Guardar Partida");
            stylizeButton(guardarPartidaButton);
            guardarPartidaButton.addActionListener(e -> {
                try {
                    laberintoPanel.guardarPartida();
                    System.out.println("Partida guardada");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            JButton recuperarPartidaButton = new JButton("Recuperar Partida");
            stylizeButton(recuperarPartidaButton);
            recuperarPartidaButton.addActionListener(e -> {
            try {
                laberintoPanel.recuperarPartida();
                System.out.println("Partida recuperada");
            }   catch (FileNotFoundException ex) {
                    Logger.getLogger(Laberinto.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(Laberinto.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            JButton reiniciarPartidaButton = new JButton("Reiniciar Partida");
            stylizeButton(reiniciarPartidaButton);
            reiniciarPartidaButton.addActionListener(e -> {
                laberintoPanel.reiniciarPartida();
                System.out.println("Partida Reiniciada");
            });

            JButton salirButton = new JButton("Salir");
            stylizeButton(salirButton);
            salirButton.addActionListener(e -> System.exit(0));

            JPanel buttonsPanel = new JPanel();
            buttonsPanel.add(guardarPartidaButton);
            buttonsPanel.add(recuperarPartidaButton);
            buttonsPanel.add(salirButton);
            buttonsPanel.add(reiniciarPartidaButton);

            frame.add(buttonsPanel, BorderLayout.SOUTH);
        });
        
    }

private static void stylizeButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLUE);
        button.setFocusPainted(false); // Para quitar el borde de enfoque
    }

    public Laberinto() {
        setFocusable(true);
        addKeyListener(this);
        colocarLetrasAleatoriasLab(laberinto, 4);
        musicaFondo();
    }
    
       private void AreaReinicio(){
        posX = 0;
        posY = 0;
        //Copia del array
        laberinto = new int[][] {
        {1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0},
        {0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1},
        {1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 5}
    };
    colocarLetrasAleatoriasLab(laberinto, 4);
    repaint();
    setFocusable(true);
    requestFocusInWindow();
    musicaFondo();
    }    
    
            private void Area1(){
        posX = 0;
        posY = 6;
        posXenemigo = 6;
        posYenemigo = 15;
        //Copia del array
        laberinto = new int[][] {
        {1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0},
        {0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1},
        {1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 5}
    };
    colocarLetrasAleatoriasLab(laberinto, 6);
    repaint();
    setFocusable(true);
    requestFocusInWindow();
    musicaFondo(); //Carga la musica
    }
    
        
    private void Area2(){
        posX = 1;
        posY = 5;
        //Copia del array
        laberinto = new int[][] {
        {1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0},
        {0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1},
        {1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 6}
    };

    colocarLetrasAleatoriasLab(laberinto,8);
    repaint();
    setFocusable(true);
    requestFocusInWindow();
    musicaFondo(); //Carga la musica
    }
    
private void colocarLetrasAleatoriasLab(int[][] laberinto, int cantidadLetras) {
    Random random = new Random();
    int letrasColocadas = 0;

    while (letrasColocadas < cantidadLetras) {
        int i = random.nextInt(laberinto.length);
        int j = random.nextInt(laberinto[0].length);

        if (laberinto[i][j] == 1) {
            laberinto[i][j] = 2;
            letrasColocadas++;
        }
    }
}

    private void guardarPartida() throws IOException {
        try (FileWriter writer = new FileWriter("partida.txt")) {
            writer.write(posX + "\n");
            writer.write(posY + "\n");
            writer.write(puntuacion + "\n");
            writer.write(nivelActual + "\n");
            writer.write(posXenemigo + "\n");
            writer.write(posYenemigo + "\n");
            writer.write(posXenemigoAnterior + "\n");
            writer.write(posYenemigoAnterior + "\n");
            
            for (int i = 0; i < laberinto.length; i++) {
                for (int j = 0; j < laberinto[i].length; j++) {
                    writer.write(laberinto[i][j] + " ");
                }
                writer.write("\n");
            }
        }
    }

private void recuperarPartida() throws FileNotFoundException, SQLException {
    // Solicitar al usuario el idPartida
    String idPartidaString = JOptionPane.showInputDialog(this, "Ingrese el ID de la partida:", "Recuperar Partida", JOptionPane.QUESTION_MESSAGE);

    // Verificar si el usuario canceló la entrada o ingresó un valor vacío
    if (idPartidaString == null || idPartidaString.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No se proporcionó un ID de partida válido. Recuperando la partida local.", "ERROR", JOptionPane.WARNING_MESSAGE);
        recuperarPartidaLocal();
        return; // Salir del método si no se proporciona un ID de partida válido
    }

    // Convertir el idPartida a entero
    int idPartida;
    try {
        idPartida = Integer.parseInt(idPartidaString);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "ID de partida inválido. Por favor, ingrese un número entero válido. Recuperando la partida local.", "ERROR", JOptionPane.WARNING_MESSAGE);
        recuperarPartidaLocal();
        return; // Salir del método si el ID de partida no es un número entero válido
    }

    // Configurar la conexión a la base de datos
    String url = "jdbc:mysql://localhost:3306/laberinto";
    String usuario = "root";
    String contraseña = "";

    boolean partidaEncontrada = false;

    try (Connection conexion = DriverManager.getConnection(url, usuario, contraseña)) {
        String consulta = "SELECT posX, posY, puntuacion, nivelActual, posXenemigo, posYenemigo,posXenemigoAnterior,posYenemigoAnterior, nombreUsuario " +
                          "FROM partidas_guardadas WHERE id = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
            pstmt.setInt(1, idPartida);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Recuperar datos de la partida desde la base de datos
                    posX = rs.getInt("posX");
                    posY = rs.getInt("posY");
                    puntuacion = rs.getInt("puntuacion");
                    nivelActual = rs.getInt("nivelActual");
                    posXenemigo = rs.getInt("posXenemigo");
                    posYenemigo = rs.getInt("posYenemigo");
                    posXenemigoAnterior = rs.getInt("posXenemigoAnterior");
                    posYenemigoAnterior = rs.getInt("posYenemigoAnterior");
                    nombreUsuario = rs.getString("nombreUsuario");
                    
                    partidaEncontrada = true;
                }
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al recuperar la partida de la base de datos. Recuperando la partida local.\n" + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        recuperarPartidaLocal();
    }
    
    if (!partidaEncontrada) {
        // Preguntar al usuario si desea cargar la partida local
        int opcion = JOptionPane.showConfirmDialog(this, "La partida con el ID proporcionado no fue encontrada en la base de datos. ¿Desea cargar la partida local?", "Partida no encontrada", JOptionPane.YES_NO_OPTION);
        
        if (opcion == JOptionPane.YES_OPTION) {
            recuperarPartidaLocal();
        }
    }

    // Repintar el componente, obtener el foco y verificar la activación de la casilla especial
    repaint();
    setFocusable(true);
    requestFocusInWindow();
    verificarActivacionCasillaEspecial();
}

private void recuperarPartidaLocal() throws FileNotFoundException {
    try (Scanner scanner = new Scanner(new File("partida.txt"))) {
        posX = scanner.nextInt();
        posY = scanner.nextInt();
        puntuacion = scanner.nextInt();
        nivelActual = scanner.nextInt();
        posXenemigo = scanner.nextInt();
        posYenemigo = scanner.nextInt();
        posXenemigoAnterior = scanner.nextInt();
        posYenemigoAnterior = scanner.nextInt();
        nombreUsuario = scanner.nextLine();

        for (int i = 0; i < laberinto.length; i++) {
            for (int j = 0; j < laberinto[i].length; j++) {
                laberinto[i][j] = scanner.nextInt();
            }
        }
            // Repintar el componente, obtener el foco y verificar la activación de la casilla especial
    repaint();
    setFocusable(true);
    requestFocusInWindow();
    verificarActivacionCasillaEspecial();
    }
}



    

    public int getPuntuacion() {
        return puntuacion;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.clearRect(0, 0, getWidth(), getHeight());

        for (int i = 0; i < laberinto.length; i++) {
            for (int j = 0; j < laberinto[i].length; j++) {
                switch (laberinto[i][j]) {
                    //Zonas negras para moverse
                    case 1:
                        g.setColor(Color.BLACK);
                        g.fillRect(j * 50, i *
                                50, 50, 50);            break;
                    //Zonas Azules que dan 10 puntos
                    case 2:
                    // Carga la imagen de la moneda desde el archivo
                    ImageIcon coinIcon = new ImageIcon("C:\\Users\\noech\\Documents\\NetBeansProjects\\Laberinto\\Laberinto\\Imagenes\\Moneda.jpg");
                    // Dibuja la imagen de la moneda en lugar del rectángulo azul
                    coinIcon.paintIcon(this, g, j * 50, i * 50);
                    break;
                    //Salida
                    case 3:
                        g.setColor(Color.GREEN);
                        g.fillRect(j * 50, i * 50, 50, 50);
                        break;
                    //Zonas Amarillas que quitan 10 puntos
                    case 4:
                        g.setColor(Color.YELLOW);
                        g.fillRect(j * 50, i * 50, 50, 50);
                        break;
                    //Zona para reemplazar la salida
                    case 5:
                        g.setColor(Color.RED); 
                        g.fillRect(j * 50, i * 50, 50, 50);
                    break;
                    //Casilla que se cambia por la de final del juego
                    case 6:
                        g.setColor(Color.RED);
                        g.fillRect(j * 50, i * 50, 50, 50);
                        break;
                    //Casilla que acaba el juego
                    case 7:
                        g.setColor(Color.GREEN);
                        g.fillRect(j * 50, i * 50, 50, 50);
                        break;    
                default:
                    break;
                }
            }
        }
        

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("X", posY * 50 + 15, posX * 50 + 30);

        g.setColor(Color.BLACK);
        g.drawString("Puntuación: " + puntuacion, 10, getHeight() - 10);
    }
    private void verificarActivacionCasillaEspecial() {
    todasRecogidas = true;

    for (int i = 0; i < laberinto.length; i++) {
        for (int j = 0; j < laberinto[i].length; j++) {
            if (laberinto[i][j] == 2) { // 2 representa una zona de puntos
                todasRecogidas = false;
                break;
            }
        }
        if (!todasRecogidas) {
            break;
        }
    }

    if (todasRecogidas) {
        // Cambia el 5 por 3 para crear la salida
        for (int i = 0; i < laberinto.length; i++) {
            for (int j = 0; j < laberinto[i].length; j++) {
                if (laberinto[i][j] == 5) { // 5 es la casilla especial
                    laberinto[i][j] = 3; // 3 representa la salida
                }
            }
        }
        // Cambia el 6 por 7 para crear la salida final
        for (int i = 0; i < laberinto.length; i++) {
            for (int j = 0; j < laberinto[i].length; j++) {
                if (laberinto[i][j] == 6) { // 6 es la casilla final
                    laberinto[i][j] = 7; // 7 representa la salida
                }
            }
        }
        detenerMusicaFondo();
        musicaSalida();
    }
}
private void moverCasillaEnemigo() {
    Random random = new Random();
    posXenemigoAnterior = posXenemigo;
    posYenemigoAnterior = posYenemigo;

    // Elimina rastro de la posición anterior
    laberinto[posXenemigoAnterior][posYenemigoAnterior] = 1;

    // Generar nueva posición aleatoria
    int nuevaPosX = posXenemigo;
    int nuevaPosY = posYenemigo;

    while (true) {
        int direccion = random.nextInt(4);
        switch (direccion) {
            case 0: // Izquierda
                nuevaPosY = Math.max(0, posYenemigo - 1);
                break;
            case 1: // Derecha
                nuevaPosY = Math.min(laberinto[0].length - 1, posYenemigo + 1);
                break;
            case 2: // Arriba
                nuevaPosX = Math.max(0, posXenemigo - 1);
                break;
            case 3: // Abajo
                nuevaPosX = Math.min(laberinto.length - 1, posXenemigo + 1);
                break;
        }

        // Verifica si la nueva posición es una zona con valor 1
        if (laberinto[nuevaPosX][nuevaPosY] == 1) {
            break;
        }
    }

    // Actualiza la nueva posición a 4 (casilla del enemigo)
    posXenemigo = nuevaPosX;
    posYenemigo = nuevaPosY;
    laberinto[posXenemigo][posYenemigo] = 4;
    
        // Si la nueva posición es la misma que la del jugador, resta 10 puntos al jugador
    if (posX == nuevaPosX && posY == nuevaPosY) {
        puntuacion -= 10;
    }
}


    private void recogerLetra() {
        switch (laberinto[posX][posY]) {
            case 2:
                puntuacion += 10;
                laberinto[posX][posY] = 1;
                musicaMonedas();
                verificarActivacionCasillaEspecial();
                break;
            case 3:
                laberinto[posX][posY] = 3;
                puntuacion += 50;
                salirDelLaberinto();
                detenerMusicaSalir();
                detenerMusicaFondo();
                break;
            case 5:
                JOptionPane.showMessageDialog(this, "Recoge todos los puntos para salir del laberinto!");
                break;
            case 6:
                JOptionPane.showMessageDialog(this, "Recoge todos los puntos para salir del laberinto!");
                break;
            case 7:
                salirDelLaberinto();
                detenerMusicaSalir();
                detenerMusicaFondo();
                break;
            default:
                break;
        }
        
    }
    
private void musicaFondo() {
    String rutaAbsolutaBGMUSIC = "C:\\Users\\noech\\Documents\\NetBeansProjects\\Laberinto\\Laberinto\\Musica\\BG_MUSIC.wav";

    try {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(rutaAbsolutaBGMUSIC).getAbsoluteFile());
        clipFondo = AudioSystem.getClip();
        clipFondo.close();
        clipFondo.open(audioInputStream);
        clipFondo.loop(Clip.LOOP_CONTINUOUSLY);
        clipFondo.start();

        FloatControl gainControl = (FloatControl) clipFondo.getControl(FloatControl.Type.MASTER_GAIN);
        float volume = 0.12f;
        float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
        gainControl.setValue(dB);
    } catch (Exception ex) {
        System.out.println("Error al reproducir el sonido: " + ex.getMessage());
    }
}
private void detenerMusicaFondo() {
    if (clipFondo != null && clipFondo.isRunning()) {
        clipFondo.stop();
        clipFondo.close();
    }
}
private void detenerMusicaSalir() {
    if (clipSalida != null && clipSalida.isRunning()) {
        clipSalida.stop();
        clipSalida.close();
    }
}

private void musicaMonedas(){
        try {
            //Efecto de sonido de moneda del super mario original
            String rutaAbsolutaPuntos  = "C:\\Users\\noech\\Documents\\NetBeansProjects\\Laberinto\\Laberinto\\Musica\\smw_coin.wav";
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(rutaAbsolutaPuntos).getAbsoluteFile());
            
            clipSalida = AudioSystem.getClip();
            clipSalida.close();
            clipSalida.open(audioInputStream); //Abre el archivo almacenado
            clipSalida.start();
            
        FloatControl gainControl = (FloatControl) clipSalida.getControl(FloatControl.Type.MASTER_GAIN); 
        //Consigue control del volumen del audio
        
        // Ajusta el volumen (-20.0 a 6.0206)
        float volume = 1.2f; // Ajusta el volumen
        float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
        gainControl.setValue(dB);
        } catch (Exception ex) {
            System.out.println("Error al reproducir el sonido: " + ex.getMessage());
        }
}
private void musicaSalida() {
    try {
        String rutaAbsolutaPuntos = "C:\\Users\\noech\\Documents\\NetBeansProjects\\Laberinto\\Laberinto\\Musica\\smw_course_clear.wav";
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(rutaAbsolutaPuntos).getAbsoluteFile());
        clipSalida = AudioSystem.getClip();
        clipSalida.open(audioInputStream);
        clipSalida.start();
        clipSalida.loop(Clip.LOOP_CONTINUOUSLY);

        FloatControl gainControl = (FloatControl) clipSalida.getControl(FloatControl.Type.MASTER_GAIN);
        //Consigue control del volumen del audio
        
        // Ajusta el volumen (-20.0 a 6.0206)
        float volume = 1.2f;
        float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
        gainControl.setValue(dB);
    } catch (Exception ex) {
        System.out.println("Error al reproducir el sonido: " + ex.getMessage());
    }
}

    
private void cargarSiguienteNivel() {
    if (nivelActual == 0) {
        Area1(); // Cargar el nivel 2
        nivelActual++;
    } else if (nivelActual == 1) {
        Area2(); // Cargar el nivel 3
        nivelActual++;
    }
}

private void salirDelLaberinto() {
    if (laberinto[posX][posY] == 3) {
        JOptionPane.showMessageDialog(this, "¡Has salido del laberinto!\nPuntuación: " + puntuacion);
        cargarSiguienteNivel(); // Carga el siguiente nivel
    } else if (laberinto[posX][posY] == 7) {
        JOptionPane.showMessageDialog(this, "¡Has Ganado el juego!"
        + "\n Puntuación Final: " + puntuacion + "\n Escriba Aquí su nombre (PLACEHOLDER)");  
        AreaReinicio();
        nivelActual = 0;
    }
}

    /**
     *
     */
    private void reiniciarPartida() {
        puntuacion = 0;
        detenerMusicaSalir();
        detenerMusicaFondo();
        AreaReinicio();

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT && posY > 0 && laberinto[posX][posY - 1] != 0) {
            posY--;
        } else if (key == KeyEvent.VK_RIGHT && posY < laberinto[0].length - 1 && laberinto[posX][posY + 1] != 0) {
            posY++;
        } else if (key == KeyEvent.VK_UP && posX > 0 && laberinto[posX - 1][posY] != 0) {
            posX--;
        } else if (key == KeyEvent.VK_DOWN && posX < laberinto.length - 1 && laberinto[posX + 1][posY] != 0) {
            posX++;
        }

        recogerLetra();//Establece los valores de las casillas
        salirDelLaberinto();//Se mueve al siguiente nivel
        moverCasillaEnemigo();//Mueve el enemigo

        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}