import java.util.Scanner;

public class Juego {

    private final Tablero tablero;

    private Pieza piezaActual;

    private volatile boolean jugando;

    private final Object lockJuego = new Object();

    private int numeroPieza;
    private int puntaje;

    public Juego() {

        tablero = new Tablero(15, 20);

        jugando = true;

        numeroPieza = 0;
        puntaje=0;

        generarNuevaPieza();
    }

    private void generarNuevaPieza() {

        TipoPieza[] tipos = TipoPieza.values();

        int indice =
                (int) (Math.random() * tipos.length);

        TipoPieza tipo =
                tipos[indice];

        char simbolo =
                (char) ('A' + (numeroPieza % 26));

        piezaActual =
                new Pieza(
                        tipo,
                        simbolo
                );

        piezaActual.setFila(0);

        int columnaInicial =
                20 / 2 - 2;

        piezaActual.setColumna(
                columnaInicial
        );

        numeroPieza++;

        System.out.println(
                "\nNueva pieza: "
                        + tipo
                        + " [" + simbolo + "]"
        );
    }

    public void iniciar() {

        Thread hiloCaida =
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        ejecutarCaida();
                    }

                });

        Thread hiloTeclado =
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        leerTeclado();
                    }

                });

        hiloTeclado.setDaemon(true);

        hiloCaida.start();
        hiloTeclado.start();

        try {

            hiloCaida.join();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }

        System.out.println(
                "\nJuego terminado."
        );

        tablero.imprimir();
    }

    private void ejecutarCaida() {

        while (jugando) {

            try {

                Thread.sleep(700);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                break;
            }

            synchronized (lockJuego) {

                if (!jugando) {
                    break;
                }

                if (
                        tablero.posicionValida(
                                piezaActual,
                                piezaActual.getFila() + 1,
                                piezaActual.getColumna()
                        )
                ) {

                    piezaActual.moverAbajo();

                } else {

                tablero.fijarPieza(piezaActual);

System.out.println(
        "\nPieza "
                + piezaActual.getSimbolo()
                + " fijada."
);

// Verificar líneas completas
int lineasEliminadas =
        tablero.eliminarLineasCompletas();

if (lineasEliminadas > 0) {

    puntaje += lineasEliminadas * 100;

    System.out.println(
            "Lineas eliminadas: "
                    + lineasEliminadas
    );

    System.out.println(
            "Puntaje actual: "
                    + puntaje
    );
}

generarNuevaPieza();

                    if (
                            !tablero.posicionValida(
                                    piezaActual,
                                    piezaActual.getFila(),
                                    piezaActual.getColumna()
                            )
                    ) {

                        System.out.println(
                                "\nGAME OVER"
                        );

                        jugando = false;
                        break;
                    }
                }

                if (jugando) {

                    tablero.imprimir(
                            piezaActual
                    );
                }
            }
        }
    }

    private void leerTeclado() {

        Scanner scanner =
                new Scanner(System.in);

        while (jugando) {

            System.out.println();
            System.out.println("Comandos:");
            System.out.println("a  = izquierda");
            System.out.println("d  = derecha");
            System.out.println("s  = bajar");
            System.out.println("g1 = giro antihorario");
            System.out.println("g2 = giro horario");
            System.out.println("q  = salir");

            System.out.print("> ");

            String comando =
                    scanner.nextLine();

            synchronized (lockJuego) {

                if (!jugando) {
                    break;
                }

                switch (comando) {

                    case "a":

                        if (
                                tablero.posicionValida(
                                        piezaActual,
                                        piezaActual.getFila(),
                                        piezaActual.getColumna() - 1
                                )
                        ) {

                            piezaActual.moverIzquierda();
                        }

                        break;

                    case "d":

                        if (
                                tablero.posicionValida(
                                        piezaActual,
                                        piezaActual.getFila(),
                                        piezaActual.getColumna() + 1
                                )
                        ) {

                            piezaActual.moverDerecha();
                        }

                        break;

                    case "s":

                        if (
                                tablero.posicionValida(
                                        piezaActual,
                                        piezaActual.getFila() + 1,
                                        piezaActual.getColumna()
                                )
                        ) {

                            piezaActual.moverAbajo();
                        }

                        break;

                    case "g1":

                        int[][] formaAnti =
                                piezaActual.rotarAntihorario();

                        if (
                                tablero.posicionValida(
                                        formaAnti,
                                        piezaActual.getFila(),
                                        piezaActual.getColumna()
                                )
                        ) {

                            piezaActual.aplicarForma(
                                    formaAnti
                            );
                        }

                        break;

                    case "g2":

                        int[][] formaHorario =
                                piezaActual.rotarHorario();

                        if (
                                tablero.posicionValida(
                                        formaHorario,
                                        piezaActual.getFila(),
                                        piezaActual.getColumna()
                                )
                        ) {

                            piezaActual.aplicarForma(
                                    formaHorario
                            );
                        }

                        break;

                    case "q":

                        jugando = false;

                        System.out.println(
                                "\nSaliendo del juego..."
                        );

                        break;

                    default:

                        System.out.println(
                                "Comando no válido."
                        );
                }

                if (jugando) {

                    tablero.imprimir(
                            piezaActual
                    );
                }
            }
        }
    }
}