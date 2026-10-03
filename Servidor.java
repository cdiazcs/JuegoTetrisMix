import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;


public class Servidor {

    private final int puerto;

    private int contadorClientes;

    private final JuegoServidor juego;

    private volatile boolean servidorActivo;

    // Clientes conectados
    private final List<ManejadorCliente> clientes;

    private boolean partidaTerminadaNotificada;

    public Servidor(int puerto) {

        this.puerto = puerto;

        contadorClientes = 0;

        juego =
                new JuegoServidor(
                        15,
                        20
                );

        clientes =
                new CopyOnWriteArrayList<ManejadorCliente>();

        partidaTerminadaNotificada = false;

        servidorActivo = true;
    }

    // Inicia el servidor
    public void iniciar() {

        System.out.println(
                "Iniciando servidor..."
        );

        // Inicia la caída automática
        iniciarHiloCaida();

        try (
                ServerSocket servidor =
                        new ServerSocket(puerto)
        ) {

            System.out.println(
                    "Servidor escuchando en puerto "
                            + puerto
            );

            while (servidorActivo) {

                System.out.println(
                        "Esperando jugador..."
                );

                Socket socketCliente =
                        servidor.accept();

                contadorClientes++;

                int idJugador =
                        contadorClientes;

                System.out.println(
                        "Jugador "
                                + idJugador
                                + " conectado desde "
                                + socketCliente.getInetAddress()
                );

                ManejadorCliente manejador =
                        new ManejadorCliente(
                                socketCliente,
                                idJugador,
                                juego,
                                this
                        );

                Thread hiloCliente =
                        new Thread(
                                manejador,
                                "Jugador-" + idJugador
                        );

                hiloCliente.start();
            }

        } catch (IOException e) {

            servidorActivo = false;

            System.out.println(
                    "Error en servidor: "
                            + e.getMessage()
            );
        }
    }

    // Añade un cliente
    public void registrarCliente(
            ManejadorCliente cliente) {

        clientes.add(
                cliente
        );

        System.out.println(
                "Clientes conectados: "
                        + clientes.size()
        );
    }

    // Quita un cliente
    public void eliminarCliente(
            ManejadorCliente cliente) {

        clientes.remove(
                cliente
        );

        System.out.println(
                "Clientes conectados: "
                        + clientes.size()
        );
    }

    // Envía el tablero a todos
    public void broadcastTablero() {

        String tableroActual =
                juego.obtenerTableroComoTexto();

        System.out.println(
                "Broadcast del tablero a "
                        + clientes.size()
                        + " jugador(es)."
        );

        for (
                ManejadorCliente cliente
                        : clientes
        ) {

            cliente.enviarTablero(
                    tableroActual
            );
        }
    }

    // Envía un mensaje a todos
    public void broadcastMensaje(
            String mensaje) {

        for (
                ManejadorCliente cliente
                        : clientes
        ) {

            cliente.enviarMensaje(
                    mensaje
            );
        }
    }

    // Informa que un jugador quedó fuera
    public void broadcastGameOver(
            int idJugador) {

        broadcastMensaje(
                "GAME_OVER "
                        + idJugador
        );
    }

    // Informa los jugadores eliminados
    public void broadcastGameOversPendientes() {

        List<Integer> jugadoresEliminados =
                juego.obtenerYLimpiarGameOversPendientes();

        for (
                Integer idJugador
                        : jugadoresEliminados
        ) {

            broadcastGameOver(
                    idJugador
            );
        }

        if (
                !jugadoresEliminados.isEmpty()
        ) {

            verificarFinPartida();
        }
    }

    // Anuncia el resultado final
    public synchronized void verificarFinPartida() {

        if (
                partidaTerminadaNotificada
                || !juego.partidaTerminada()
        ) {

            return;
        }

        int ganador =
                juego.obtenerGanador();

        if (ganador >= 0) {

            broadcastMensaje(
                    "FIN_PARTIDA GANADOR "
                            + ganador
            );

        } else if (ganador == -2) {

            broadcastMensaje(
                    "FIN_PARTIDA EMPATE"
            );

        } else {

            broadcastMensaje(
                    "FIN_PARTIDA SIN_GANADOR"
            );
        }

        partidaTerminadaNotificada = true;
    }

    // Inicia la caída automática
    private void iniciarHiloCaida() {

        Thread hiloCaida =
                new Thread(
                        new Runnable() {

                            @Override
                            public void run() {

                                while (servidorActivo) {

                                    try {

                                        Thread.sleep(
                                                700
                                        );

                                    } catch (
                                            InterruptedException e
                                    ) {

                                        Thread.currentThread()
                                                .interrupt();

                                        break;
                                    }

                                    if (
                                            juego.estaPartidaFinalizada()
                                    ) {

                                        break;
                                    }

                                    // Espera a que haya clientes
                                    if (clientes.isEmpty()) {

                                        continue;
                                    }

                                    // Avanza las piezas
                                    boolean huboCambio =
                                            juego
                                                .avanzarCaidaAutomatica();

                                    // Envía el estado actualizado
                                    if (huboCambio) {

                                        System.out.println(
                                                "Caida automatica -> broadcast."
                                        );

                                        broadcastTablero();
                                        broadcastPuntajes();
                                        broadcastTurno();
                                        broadcastGameOversPendientes();
                                        verificarFinPartida();
                                    }
                                }
                            }
                        },
                        "Hilo-Caida-Servidor"
                );

        // Permite cerrar la aplicación
        hiloCaida.setDaemon(true);

        hiloCaida.start();

        System.out.println(
                "Hilo de caida automatica iniciado."
        );
    }

    // Envía el turno actual
    public void broadcastTurno() {

        int jugadorTurno =
                juego.getJugadorTurno();

        if (jugadorTurno != -1) {

            broadcastMensaje(
                    "TURNO Jugador "
                            + jugadorTurno
            );
        }
    }

    // Envía los puntajes
    public void broadcastPuntajes() {

    Map<Integer, Integer> copia =
            juego.obtenerCopiaPuntajes();

    for (
            Map.Entry<Integer, Integer> entrada
                    : copia.entrySet()
    ) {

        int idJugador =
                entrada.getKey();

        int puntos =
                entrada.getValue();

        broadcastMensaje(
                "PUNTAJE "
                        + idJugador
                        + " "
                        + puntos
        );
    }
}
}
