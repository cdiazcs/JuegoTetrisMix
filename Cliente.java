import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    // Servidor
    private final String ipServidor;
    private final int puerto;

    public Cliente(
            String ipServidor,
            int puerto) {

        this.ipServidor =
                ipServidor;

        this.puerto =
                puerto;
    }

    // Conecta al servidor
    public void conectar() {

        System.out.println(
                "Conectando al servidor..."
        );

        try {

            // Conexión
            Socket socket =
                    new Socket(
                            ipServidor,
                            puerto
                    );

            // Entrada y salida
            BufferedReader entrada =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );

            PrintWriter salida =
                    new PrintWriter(
                            socket.getOutputStream(),
                            true
                    );

            // Entrada del usuario
            Scanner scanner =
                    new Scanner(
                            System.in
                    );

            System.out.println(
                    "Conexion establecida."
            );

            // Recibe mensajes
            Thread hiloReceptor =
                    new Thread(
                            new Runnable() {

                                @Override
                                public void run() {

                                    escucharServidor(
                                            entrada
                                    );
                                }
                            },
                            "ReceptorServidor"
                    );

            hiloReceptor.start();

            // Envía comandos
            while (true) {

                System.out.print(
                        "> "
                );

                String mensaje =
                        scanner
                                .nextLine()
                                .trim();

                if (
                        mensaje.length()
                                == 0
                ) {

                    continue;
                }

                salida.println(
                        mensaje
                );

                // Salir
                if (
                        mensaje.equalsIgnoreCase(
                                "SALIR"
                        )
                ) {

                    try {

                        hiloReceptor.join(
                                1000
                        );

                    } catch (
                            InterruptedException e
                    ) {

                        Thread.currentThread()
                                .interrupt();
                    }

                    break;
                }
            }

            scanner.close();

            // Cierra el socket
            socket.close();

        } catch (IOException e) {

            System.out.println(
                    "No se pudo conectar: "
                            + e.getMessage()
            );
        }
    }

    // Lee mensajes
    private void escucharServidor(
            BufferedReader entrada) {

        try {

            String linea;

            while (
                    (linea =
                            entrada.readLine())
                            != null
            ) {

                // Lee el tablero
                if (
                        linea.equals(
                                "INICIO_TABLERO"
                        )
                ) {

                    leerTableroDesdeInicio(
                            entrada
                    );

                    continue;
                }

                // Muestra el mensaje
                System.out.println(
                        "\n"
                                + linea
                );

                if (
                        linea.equals(
                                "DESCONECTADO"
                        )
                ) {

                    break;
                }
            }

        } catch (IOException e) {

            System.out.println(
                    "\nConexion con servidor finalizada."
            );
        }
    }

    // Muestra el tablero
    private void leerTableroDesdeInicio(
            BufferedReader entrada)
            throws IOException {

        System.out.println();

        System.out.println(
                "===== TABLERO ====="
        );

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

            System.out.println(
                    linea
            );
        }

        System.out.println(
                "==================="
        );

        System.out.println();

        System.out.print(
                "> "
        );
    }
}