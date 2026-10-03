import javax.swing.SwingUtilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;


import java.net.Socket;

public class ClienteGUI {

    private Socket socket;

    private BufferedReader entrada;

    private PrintWriter salida;

    private final VentanaCliente ventana;

    private volatile boolean conectado;
    private int idJugador;


    public ClienteGUI(
            VentanaCliente ventana) {

        this.ventana =
                ventana;

        conectado =
                false;
        idJugador = -1;
    }


    // Conecta al servidor

    public void conectar(
            String ip,
            int puerto) {

        if (conectado) {
            return;
        }


        Thread hiloConexion =
                new Thread(
                        new Runnable() {

                            @Override
                            public void run() {

                                try {

                                    actualizarEstado(
                                            "Conectando..."
                                    );


                                    socket =
                                            new Socket(
                                                    ip,
                                                    puerto
                                            );


                                    entrada =
                                            new BufferedReader(
                                                    new InputStreamReader(
                                                            socket.getInputStream()
                                                    )
                                            );


                                    salida =
                                            new PrintWriter(
                                                    socket.getOutputStream(),
                                                    true
                                            );


                                    conectado =
                                            true;


                                    actualizarEstado(
                                            "Conectado"
                                    );


                                    // Escucha al servidor
                                    escucharServidor();


                                } catch (
                                        IOException e
                                ) {

                                    conectado =
                                            false;


                                    actualizarEstado(
                                            "No se pudo conectar: "
                                                    + e.getMessage()
                                    );


                                    permitirNuevaConexion();
                                }
                            }
                        },
                        "ClienteGUI-Receptor"
                );


        hiloConexion.start();
    }


    // Recibe mensajes del servidor

    private void escucharServidor() {

        try {

            String linea;


            while (
                    conectado
                    &&
                    (linea =
                            entrada.readLine())
                            != null
            ) {

                procesarMensaje(
                        linea
                );
            }


        } catch (
                IOException e
        ) {

            if (
                    conectado
            ) {

                actualizarEstado(
                        "Conexión perdida"
                );
            }

        } finally {

            conectado =
                    false;

            habilitarControles(
                    false
            );


            permitirNuevaConexion();


            actualizarEstado(
                    "Desconectado"
            );
        }
    }


    // Procesa cada mensaje

    private void procesarMensaje(
            String linea)
            throws IOException {

        // Asigna el ID recibido
        if (
        linea.startsWith(
                "BIENVENIDO "
        )
) {

    String textoId =
            linea.substring(
                    "BIENVENIDO ".length()
            )
            .trim();

    try {

        idJugador =
                Integer.parseInt(
                        textoId
                );

    } catch (
            NumberFormatException e
    ) {

        actualizarEstado(
                "ID de jugador inválido"
        );

        return;
    }

    final String textoJugador =
            String.valueOf(
                    idJugador
            );

    SwingUtilities.invokeLater(
            new Runnable() {

                @Override
                public void run() {

                    ventana.actualizarJugador(
                            textoJugador
                    );

                    ventana.actualizarEstado(
                            "Conectado como Jugador "
                                    + textoJugador
                    );
                }
            }
    );

    return;
}


        // Recibe el tablero
        if (
                linea.equals(
                        "INICIO_TABLERO"
                )
        ) {

            leerTablero();

            return;
        }


        // Actualiza el turno
        if (
                linea.startsWith(
                        "TURNO Jugador "
                )
        ) {

            String textoId =
                    linea.substring(
                            "TURNO Jugador ".length()
                    )
                    .trim();

            try {

                int idTurno =
                        Integer.parseInt(
                                textoId
                        );

                if (
                        idTurno == idJugador
                ) {

                    actualizarTurno(
                            "TU TURNO"
                    );

                    habilitarControles(
                            true
                    );

                } else {

                    actualizarTurno(
                            "Jugador "
                                    + idTurno
                    );

                    habilitarControles(
                            false
                    );
                }

            } catch (
                    NumberFormatException ignored
            ) {
            }

            return;
        }


        // Confirma el movimiento
        if (
                linea.startsWith(
                        "OK - Turno Jugador "
                )
        ) {

            String numero =
                    linea.substring(
                            "OK - Turno Jugador ".length()
                    )
                    .trim();


            actualizarTurno(
                    "Jugador "
                            + numero
            );


            actualizarEstado(
                    "Movimiento realizado"
            );


            return;
        }


        // Informa si no es su turno
        if (
                linea.startsWith(
                        "NO_ES_TU_TURNO"
                )
        ) {

            actualizarEstado(
                    linea
            );


            // Busca el turno actual
            String textoBuscado =
                    "Turno del jugador ";


            int posicion =
                    linea.indexOf(
                            textoBuscado
                    );


            if (
                    posicion >= 0
            ) {

                String numero =
                        linea.substring(
                                posicion
                                        + textoBuscado.length()
                        )
                        .trim();


                actualizarTurno(
                        "Jugador "
                                + numero
                );
            }


            return;
        }


        // Desconexión
        if (
                linea.equals(
                        "DESCONECTADO"
                )
        ) {

            conectado =
                    false;

            actualizarEstado(
                    "Desconectado"
            );

            habilitarControles(
                    false
            );

            return;
        }

        if (
                linea.equals(
                        "PARTIDA_FINALIZADA"
                )
        ) {

            actualizarEstado(
                    "La partida ya terminó"
            );

            habilitarControles(
                    false
            );

            return;
        }

        // Fin de partida
        if (
                linea.startsWith(
                        "FIN_PARTIDA "
                )
        ) {

            if (
                    linea.startsWith(
                            "FIN_PARTIDA GANADOR "
                    )
            ) {

                String textoId =
                        linea.substring(
                                "FIN_PARTIDA GANADOR ".length()
                        )
                        .trim();

                try {

                    int idGanador =
                            Integer.parseInt(
                                    textoId
                            );

                    if (
                            idGanador == idJugador
                    ) {

                        actualizarEstado(
                                "FIN DE PARTIDA - GANASTE"
                        );

                    } else {

                        actualizarEstado(
                                "FIN DE PARTIDA - Ganador Jugador "
                                        + idGanador
                        );
                    }

                } catch (
                        NumberFormatException ignored
                ) {

                    actualizarEstado(
                            "Mensaje FIN_PARTIDA inválido"
                    );
                }

            } else if (
                    linea.equals(
                            "FIN_PARTIDA EMPATE"
                    )
            ) {

                actualizarEstado(
                        "FIN DE PARTIDA - EMPATE"
                );

            } else if (
                    linea.equals(
                            "FIN_PARTIDA SIN_GANADOR"
                    )
            ) {

                actualizarEstado(
                        "FIN DE PARTIDA - SIN GANADOR"
                );

            } else {

                actualizarEstado(
                        "Mensaje FIN_PARTIDA inválido"
                );
            }

            habilitarControles(
                    false
            );

            return;
        }


        // Jugador eliminado
        if (
                linea.startsWith(
                        "GAME_OVER "
                )
        ) {

            String textoId =
                    linea.substring(
                            "GAME_OVER ".length()
                    )
                    .trim();

            try {

                int idEliminado =
                        Integer.parseInt(
                                textoId
                        );

                if (
                        idEliminado == idJugador
                ) {

                    actualizarEstado(
                            "GAME OVER"
                    );

                    habilitarControles(
                            false
                    );

                } else {

                    actualizarEstado(
                            "Jugador "
                                    + idEliminado
                                    + " quedó fuera de la partida"
                    );
                }

            } catch (
                    NumberFormatException ignored
            ) {

                actualizarEstado(
                        "Mensaje GAME_OVER inválido"
                );
            }

            return;
        }


        // Mensaje anterior de fin de juego
        if (
                linea.contains(
                        "GAME OVER"
                )
        ) {

            actualizarEstado(
                    linea
            );

            habilitarControles(
                    false
            );

            return;
        }


        // Ignora la lista de comandos
        if (
                linea.startsWith(
                        "Comandos:"
                )
        ) {

            return;
        }


        // Actualiza el puntaje
        if (
                linea.startsWith(
                        "PUNTAJE "
                )
        ) {

            String[] partes =
                    linea.trim().split(
                            "\\s+"
                    );

            if (
                    partes.length >= 3
            ) {

                try {

                    int jugadorMensaje =
                            Integer.parseInt(
                                    partes[1]
                            );

                    int puntos =
                            Integer.parseInt(
                                    partes[2]
                            );

                    if (
                            jugadorMensaje == idJugador
                    ) {

                        actualizarPuntaje(
                                puntos
                        );
                    }

                } catch (
                        NumberFormatException ignored
                ) {
                }
            }

            return;
        }


        // Muestra otros mensajes
        if (
                !linea.trim().isEmpty()
        ) {

            actualizarEstado(
                    linea
            );
        }
    }


    // Lee el tablero completo

    private void leerTablero()
            throws IOException {

        StringBuilder tablero =
                new StringBuilder();


        String linea;


        while (
                (linea =
                        entrada.readLine())
                        != null
        ) {

            if (
                    linea.equals(
                            "FIN_TABLERO"
                    )
            ) {

                break;
            }


            tablero.append(
                    linea
            );

            tablero.append(
                    "\n"
            );
        }


        final String textoTablero =
                tablero.toString();


        SwingUtilities.invokeLater(
                new Runnable() {

                    @Override
                    public void run() {

                        ventana.actualizarTablero(
                                textoTablero
                        );
                    }
                }
        );
    }


    // Envía un comando

    public void enviarComando(
            String comando) {

        if (
                !conectado
                ||
                salida == null
        ) {

            actualizarEstado(
                    "No conectado al servidor"
            );

            return;
        }


        salida.println(
                comando
        );


        salida.flush();


        actualizarEstado(
                "Comando enviado: "
                        + comando
        );
    }


    // Cierra la conexión

    public void desconectar() {

        // Avisa al servidor
        if (
                salida != null
                &&
                conectado
        ) {

            salida.println(
                    "SALIR"
            );

            salida.flush();
        }


        conectado =
                false;


        try {

            if (
                    entrada != null
            ) {

                entrada.close();
            }


        } catch (
                IOException ignored
        ) {
        }


        if (
                salida != null
        ) {

            salida.close();
        }


        try {

            if (
                    socket != null
                    &&
                    !socket.isClosed()
            ) {

                socket.close();
            }


        } catch (
                IOException ignored
        ) {
        }
    }


    // Indica si sigue conectado

    public boolean estaConectado() {

        return conectado;
    }


    // Actualiza la interfaz

    private void actualizarEstado(
            final String texto) {

        SwingUtilities.invokeLater(
                new Runnable() {

                    @Override
                    public void run() {

                        ventana.actualizarEstado(
                                texto
                        );
                    }
                }
        );
    }


    private void actualizarTurno(
            final String turno) {

        SwingUtilities.invokeLater(
                new Runnable() {

                    @Override
                    public void run() {

                        ventana.actualizarTurno(
                                turno
                        );
                    }
                }
        );
    }


    private void habilitarControles(
            final boolean habilitar) {

        SwingUtilities.invokeLater(
                new Runnable() {

                    @Override
                    public void run() {

                        ventana.habilitarControlesJuego(
                                habilitar
                        );
                    }
                }
        );
    }


    private void permitirNuevaConexion() {

        SwingUtilities.invokeLater(
                new Runnable() {

                    @Override
                    public void run() {

                        ventana.permitirNuevaConexion();
                    }
                }
        );
    }
    private void actualizarPuntaje(
        final int puntaje) {

    SwingUtilities.invokeLater(
            new Runnable() {

                @Override
                public void run() {

                    ventana.actualizarPuntaje(
                            puntaje
                    );
                }
            }
    );
}
}