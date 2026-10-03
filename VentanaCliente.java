import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VentanaCliente extends JFrame {

    // Cliente de red

    private ClienteGUI clienteGUI;


    // Datos de conexión

    private JTextField txtIP;
    private JTextField txtPuerto;

    private JButton btnConectar;


    // Datos del jugador

    private JLabel lblJugador;
    private JLabel lblTurno;
    private JLabel lblPuntaje;
    private JLabel lblConexion;
    private JLabel lblEstado;

    private Font fuenteTurnoNormal;
    private Color colorTurnoNormal;


    // Tablero

    private PanelTablero panelTableroGrafico;


    // Controles

    private JButton btnIzquierda;
    private JButton btnDerecha;
    private JButton btnAbajo;

    private JButton btnG1;
    private JButton btnG2;

    private boolean controlesJuegoHabilitados;


    // Crea la ventana

    public VentanaCliente() {

        configurarVentana();

        crearComponentes();

        organizarInterfaz();

        clienteGUI =
                new ClienteGUI(
                        this
                );

        configurarEventos();
    }


    // Configura la ventana

    private void configurarVentana() {

        setTitle(
                "Tetris Mix Multiplayer"
        );

        setSize(
                900,
                720
        );

        // Cierra la conexión al salir
        setDefaultCloseOperation(
                JFrame.DO_NOTHING_ON_CLOSE
        );

        setLocationRelativeTo(
                null
        );

        setMinimumSize(
                new Dimension(
                        800,
                        650
                )
        );


        addWindowListener(
                new WindowAdapter() {

                    @Override
                    public void windowClosing(
                            WindowEvent e) {

                        if (
                                clienteGUI != null
                        ) {

                            clienteGUI.desconectar();
                        }

                        dispose();

                        System.exit(
                                0
                        );
                    }
                }
        );
    }


    // Crea los componentes

    private void crearComponentes() {

        // Datos de conexión

        txtIP =
                new JTextField(
                        "localhost",
                        12
                );

        txtPuerto =
                new JTextField(
                        "5000",
                        6
                );

        btnConectar =
                new JButton(
                        "Conectar"
                );


        // Información del jugador

        lblJugador =
                new JLabel("Jugador: -");

        lblJugador.setFont(
                lblJugador.getFont().deriveFont(
                        Font.BOLD,
                        16f
                )
        );

        lblTurno =
                new JLabel("Turno actual: -");

        lblTurno.setFont(
                lblTurno.getFont().deriveFont(
                        Font.PLAIN,
                        14f
                )
        );

        fuenteTurnoNormal =
                lblTurno.getFont();

        colorTurnoNormal =
                lblTurno.getForeground();

        lblPuntaje =
                new JLabel("Puntaje: 0");

        lblPuntaje.setFont(
                lblPuntaje.getFont().deriveFont(
                        Font.PLAIN,
                        14f
                )
        );

        lblConexion =
                new JLabel("Desconectado");

        lblConexion.setFont(
                lblConexion.getFont().deriveFont(
                        Font.BOLD,
                        14f
                )
        );

        lblEstado =
                new JLabel("Estado: Listo");

        lblEstado.setFont(
                lblEstado.getFont().deriveFont(
                        Font.PLAIN,
                        14f
                )
        );


        // Tablero

        panelTableroGrafico =
                new PanelTablero(
                        15,
                        20
        );


        // Botones de juego

        btnIzquierda =
                new JButton(
                        "←"
                );

        btnDerecha =
                new JButton(
                        "→"
                );

        btnAbajo =
                new JButton(
                        "↓"
                );

        btnG1 =
                new JButton(
                        "G1"
                );

        btnG2 =
                new JButton(
                        "G2"
                );


        // Ajusta el tamaño de los botones
        Dimension tamControl =
                new Dimension(
                        54,
                        42
                );

        btnIzquierda.setPreferredSize(
                tamControl
        );

        btnDerecha.setPreferredSize(
                tamControl
        );

        btnAbajo.setPreferredSize(
                tamControl
        );

        btnG1.setPreferredSize(
                tamControl
        );

        btnG2.setPreferredSize(
                tamControl
        );

        btnIzquierda.setToolTipText("Mover izquierda");
        btnDerecha.setToolTipText("Mover derecha");
        btnAbajo.setToolTipText("Bajar");
        btnG1.setToolTipText("Giro antihorario");
        btnG2.setToolTipText("Giro horario");


        // Espera a conectarse
        habilitarControlesJuego(
                false
        );
    }


    // Organiza la interfaz

    private void organizarInterfaz() {

        JPanel panelPrincipal =
                new JPanel(
                        new BorderLayout(
                                12,
                                12
                        )
                );

        panelPrincipal.setBorder(
                new EmptyBorder(
                        16,
                        16,
                        16,
                        16
                )
        );


        // Panel de conexión

        JPanel panelConexion =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                8,
                                4
                        )
                );


        panelConexion.add(
                new JLabel(
                        "IP servidor:"
                )
        );

        panelConexion.add(
                txtIP
        );


        panelConexion.add(
                new JLabel(
                        "Puerto:"
                )
        );

        panelConexion.add(
                txtPuerto
        );


        panelConexion.add(
                btnConectar
        );


        // Panel del tablero

        JPanel panelTablero =
                new JPanel(
                        new BorderLayout()
                );

        TitledBorder bordeTablero =
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(),
                        "Tablero"
                );

        bordeTablero.setTitleFont(
                lblJugador.getFont().deriveFont(
                        Font.BOLD,
                        16f
                )
        );

        panelTablero.setBorder(
                BorderFactory.createCompoundBorder(
                        bordeTablero,
                        new EmptyBorder(
                                8,
                                8,
                                8,
                                8
                        )
                )
        );


        JPanel panelTableroCentrado =
                new JPanel(
                        new GridBagLayout()
                );

        panelTableroCentrado.add(
                panelTableroGrafico
        );

        panelTablero.add(
                panelTableroCentrado,
                BorderLayout.CENTER
        );


        // Panel del juego

        JPanel panelDerecho =
                new JPanel();

        panelDerecho.setPreferredSize(
                new Dimension(
                        230,
                        0
                )
        );


        panelDerecho.setLayout(
                new BoxLayout(
                        panelDerecho,
                        BoxLayout.Y_AXIS
                )
        );


        panelDerecho.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(
                                "Juego"
                        ),
                        new EmptyBorder(
                                10,
                                10,
                                10,
                                10
                        )
                )
        );


        JPanel panelInformacion =
                new JPanel(
                        new GridLayout(
                                0,
                                1,
                                0,
                                6
                        )
                );

        panelInformacion.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        TitledBorder bordeInformacion =
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(),
                        "Información del jugador"
                );

        bordeInformacion.setTitleFont(
                lblJugador.getFont().deriveFont(
                        Font.BOLD,
                        16f
                )
        );

        panelInformacion.setBorder(
                BorderFactory.createCompoundBorder(
                        bordeInformacion,
                        new EmptyBorder(
                                8,
                                8,
                                8,
                                8
                        )
                )
        );

        panelInformacion.add(
                lblJugador
        );

        panelInformacion.add(
                lblTurno
        );

        panelInformacion.add(
                lblPuntaje
        );

        panelInformacion.add(
                lblConexion
        );

        panelDerecho.add(
                panelInformacion
        );

        panelDerecho.add(
                Box.createVerticalStrut(
                        12
                )
        );


        // Botón de giro

        btnG1.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        panelDerecho.add(
                btnG1
        );

        panelDerecho.add(
                Box.createVerticalStrut(
                        8
                )
        );


        // Botones de movimiento

        JPanel panelMovimiento =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                3,
                                3
                        )
                );


        panelMovimiento.add(
                btnIzquierda
        );

        panelMovimiento.add(
                btnAbajo
        );

        panelMovimiento.add(
                btnDerecha
        );


        panelDerecho.add(
                panelMovimiento
        );

        panelDerecho.add(
                Box.createVerticalStrut(
                        8
                )
        );


        // Segundo botón de giro

        btnG2.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        panelDerecho.add(
                btnG2
        );


        // Estado del juego

        JPanel panelEstado =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                8,
                                4
                        )
                );

        panelEstado.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEtchedBorder(),
                        new EmptyBorder(
                                4,
                                8,
                                4,
                                8
                        )
                )
        );

        panelEstado.add(
                lblEstado
        );


        // Une los paneles

        panelPrincipal.add(
                panelConexion,
                BorderLayout.NORTH
        );

        panelPrincipal.add(
                panelTablero,
                BorderLayout.CENTER
        );

        panelPrincipal.add(
                panelDerecho,
                BorderLayout.EAST
        );

        panelPrincipal.add(
                panelEstado,
                BorderLayout.SOUTH
        );


        setContentPane(
                panelPrincipal
        );
    }


    // Configura los eventos

    private void configurarEventos() {

        // Conexión
        btnConectar.addActionListener(
                e -> conectarServidor()
        );


        // Controles del juego
        btnIzquierda.addActionListener(
                e -> clienteGUI.enviarComando(
                        "A"
                )
        );

        btnDerecha.addActionListener(
                e -> clienteGUI.enviarComando(
                        "D"
                )
        );

        btnAbajo.addActionListener(
                e -> clienteGUI.enviarComando(
                        "S"
                )
        );

        btnG1.addActionListener(
                e -> clienteGUI.enviarComando(
                        "G1"
                )
        );

        btnG2.addActionListener(
                e -> clienteGUI.enviarComando(
                        "G2"
                )
        );

        registrarAtajo(
                "LEFT",
                "mover-izquierda",
                btnIzquierda
        );

        registrarAtajo(
                "RIGHT",
                "mover-derecha",
                btnDerecha
        );

        registrarAtajo(
                "DOWN",
                "mover-abajo",
                btnAbajo
        );

        registrarAtajo(
                "Z",
                "rotar-g1",
                btnG1
        );

        registrarAtajo(
                "X",
                "rotar-g2",
                btnG2
        );
    }

    private void registrarAtajo(
            String tecla,
            String nombreAccion,
            final JButton boton) {

        getRootPane()
                .getInputMap(
                        JComponent.WHEN_IN_FOCUSED_WINDOW
                )
                .put(
                        KeyStroke.getKeyStroke(tecla),
                        nombreAccion
                );

        getRootPane()
                .getActionMap()
                .put(
                        nombreAccion,
                        new AbstractAction() {

                            @Override
                            public void actionPerformed(
                                    java.awt.event.ActionEvent evento) {

                                if (controlesJuegoHabilitados) {

                                    boton.doClick();
                                }
                            }
                        }
                );
    }


    // Conecta con el servidor

    private void conectarServidor() {

        String ip =
                txtIP
                        .getText()
                        .trim();


        String textoPuerto =
                txtPuerto
                        .getText()
                        .trim();


        if (
                ip.isEmpty()
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese la IP del servidor.",
                    "Conexión",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        int puerto;


        try {

            puerto =
                    Integer.parseInt(
                            textoPuerto
                    );

        } catch (
                NumberFormatException e
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "El puerto debe ser un número.",
                    "Puerto inválido",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }


        if (
                puerto < 1
                ||
                puerto > 65535
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "El puerto debe estar entre 1 y 65535.",
                    "Puerto inválido",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }


        if (
                clienteGUI.estaConectado()
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "El cliente ya está conectado."
            );

            return;
        }


        btnConectar.setEnabled(
                false
        );

        txtIP.setEnabled(
                false
        );

        txtPuerto.setEnabled(
                false
        );


        actualizarEstado(
                "Conectando a "
                        + ip
                        + ":"
                        + puerto
                        + "..."
        );


        clienteGUI.conectar(
                ip,
                puerto
        );
    }


    // Actualiza la interfaz

    public void actualizarJugador(
            String jugador) {

        lblJugador.setText(
                "Jugador: "
                        + jugador
        );
    }


    public void actualizarTurno(
            String turno) {

        lblTurno.setText(
                "Turno actual: "
                        + turno
        );

        if ("TU TURNO".equals(turno)) {

            lblTurno.setFont(
                    fuenteTurnoNormal.deriveFont(
                            Font.BOLD,
                            18f
                    )
            );

            lblTurno.setForeground(
                    new Color(46, 125, 50)
            );

        } else {

            lblTurno.setFont(
                    fuenteTurnoNormal
            );

            lblTurno.setForeground(
                    colorTurnoNormal
            );
        }
    }


    public void actualizarPuntaje(
            int puntaje) {

        lblPuntaje.setText(
                "Puntaje: "
                        + puntaje
        );
    }


    public void actualizarEstado(
            String estado) {

        if (
                "Conectado".equals(estado)
                        || estado.startsWith("Conectado como Jugador ")
        ) {

            lblConexion.setText("Conectado");
            lblConexion.setForeground(
                    new Color(46, 125, 50)
            );

        } else if (
                "Desconectado".equals(estado)
                        || "Conexión perdida".equals(estado)
                        || estado.startsWith("No se pudo conectar:")
        ) {

            lblConexion.setText("Desconectado");
            lblConexion.setForeground(
                    new Color(183, 28, 28)
            );

        } else if ("GAME OVER".equals(estado)) {

            lblConexion.setText("GAME OVER");
            lblConexion.setForeground(
                    new Color(183, 28, 28)
            );
        }

        if (
                !"Conectado".equals(estado)
                        && !"Desconectado".equals(estado)
        ) {

            lblEstado.setText(
                    "Estado: "
                            + estado
            );
        }
    }


    public void actualizarTablero(
            String tablero) {

        panelTableroGrafico.actualizarDesdeTexto(
                tablero
        );
    }


    public void habilitarControlesJuego(
            boolean habilitar) {

        controlesJuegoHabilitados = habilitar;

        btnIzquierda.setEnabled(
                habilitar
        );

        btnDerecha.setEnabled(
                habilitar
        );

        btnAbajo.setEnabled(
                habilitar
        );

        btnG1.setEnabled(
                habilitar
        );

        btnG2.setEnabled(
                habilitar
        );
    }


    // Permite intentar otra conexión
    public void permitirNuevaConexion() {

        btnConectar.setEnabled(
                true
        );

        txtIP.setEnabled(
                true
        );

        txtPuerto.setEnabled(
                true
        );

        habilitarControlesJuego(
                false
        );
    }

}