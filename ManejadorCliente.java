import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ManejadorCliente implements Runnable {

    private final Socket socket;

    private final int idJugador;

    private final JuegoServidor juego;

    private final Servidor servidor;

    // Salida para enviar mensajes al cliente
    private PrintWriter salida;

    public ManejadorCliente(
            Socket socket,
            int idJugador,
            JuegoServidor juego,
            Servidor servidor) {

        this.socket = socket;

        this.idJugador = idJugador;

        this.juego = juego;

        this.servidor = servidor;
    }

    @Override
    public void run() {

        System.out.println(
                "Hilo iniciado para jugador "
                        + idJugador
        );

        try {

            BufferedReader entrada =
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

            // Registra al jugador
            juego.registrarJugador(
                    idJugador
            );

            // Guarda la conexión
            servidor.registrarCliente(
                    this
            );

            enviarMensaje(
                    "BIENVENIDO "
                            + idJugador
            );

            enviarMensaje(
                    "Comandos: A D S G1 G2 TABLERO SALIR"
            );

            servidor.broadcastGameOversPendientes();

            // Actualiza el tablero para todos
            servidor.broadcastTablero();

            String mensaje;

            while (
                    (mensaje = entrada.readLine())
                            != null
            ) {

                mensaje =
                        mensaje
                                .trim()
                                .toUpperCase();

                System.out.println(
                        "Jugador "
                                + idJugador
                                + ": "
                                + mensaje
                );

                // Cierra la conexión
                if (
                        mensaje.equals("SALIR")
                ) {

                    enviarMensaje(
                            "DESCONECTADO"
                    );

                    break;
                }

                // Envía el tablero solicitado
                if (
                        mensaje.equals("TABLERO")
                ) {

                    enviarTablero(
                            juego.obtenerTableroComoTexto()
                    );

                    continue;
                }

                // Procesa el comando
                String respuesta =
                        juego.procesarComando(
                                idJugador,
                                mensaje
                        );

                enviarMensaje(
                        respuesta
                );

                if (
                        respuesta.equals("PARTIDA_FINALIZADA")
                ) {

                    continue;
                }

                // Actualiza el juego para todos
                if (
                        esComandoMovimiento(
                                mensaje
                        )
                ) {

                    servidor.broadcastTablero();
                    servidor.broadcastPuntajes();

                    if (
                            respuesta.startsWith("OK -")
                    ) {

                        servidor.broadcastTurno();
                    }

                    servidor.broadcastGameOversPendientes();
                }
            }

        } catch (IOException e) {

            System.out.println(
                    "Jugador "
                            + idJugador
                            + " desconectado inesperadamente."
            );

        } finally {

            // Quita la pieza del jugador
            juego.desregistrarJugador(
                    idJugador
            );

            // Quita la conexión
            servidor.eliminarCliente(
                    this
            );

            // Actualiza el juego para los demás
            servidor.broadcastTablero();

             servidor.broadcastTurno();

             servidor.broadcastGameOversPendientes();

            servidor.verificarFinPartida();

            try {

                socket.close();

            } catch (IOException e) {

                System.out.println(
                        "Error cerrando socket del jugador "
                                + idJugador
                );
            }

            System.out.println(
                    "Hilo del jugador "
                            + idJugador
                            + " finalizado."
            );
        }
    }

    private boolean esComandoMovimiento(
            String mensaje) {

        return mensaje.equals("A")
                || mensaje.equals("D")
                || mensaje.equals("S")
                || mensaje.equals("G1")
                || mensaje.equals("G2");
    }

    // Envía un mensaje al cliente
    public synchronized void enviarMensaje(
            String mensaje) {

        if (salida != null) {

            salida.println(
                    mensaje
            );

            salida.flush();
        }
    }

    // Envía el tablero completo
    public synchronized void enviarTablero(
            String tablero) {

        if (salida == null) {
            return;
        }

        salida.println(
                "INICIO_TABLERO"
        );

        salida.print(
                tablero
        );

        salida.println(
                "FIN_TABLERO"
        );

        salida.flush();
    }
}