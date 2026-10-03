import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class JuegoServidor {

    // Datos del juego.

    private final Tablero tablero;

    private final Object lockJuego =
            new Object();

    private final Map<Integer, Pieza> piezasJugadores;

    private final List<Integer> ordenJugadores;

    private final Map<Integer, Integer> puntajes;

    private final Set<Integer> jugadoresEliminados;

    private final List<Integer> gameOversPendientes;

    private boolean partidaFinalizada;

    private int indiceTurno;

    private Integer jugadorTurno;


    // Prepara el juego

    public JuegoServidor(
            int filas,
            int columnas) {

        tablero =
                new Tablero(
                        filas,
                        columnas
                );

        piezasJugadores =
                new HashMap<Integer, Pieza>();

        ordenJugadores =
                new ArrayList<Integer>();

        puntajes =
                new HashMap<Integer, Integer>();

        jugadoresEliminados =
                new HashSet<Integer>();

        gameOversPendientes =
                new ArrayList<Integer>();

        partidaFinalizada = false;

        indiceTurno = 0;

        jugadorTurno = null;
    }


    // Registra jugadores

    public void registrarJugador(
            int idJugador) {

        synchronized (lockJuego) {

            // Evita duplicados
            if (
                    ordenJugadores.contains(
                            idJugador
                    )
            ) {

                return;
            }


            // Guarda el orden de entrada
            ordenJugadores.add(
                    idJugador
            );


            // Inicia el puntaje
            puntajes.put(
                    idJugador,
                    0
            );


            System.out.println(
                    "Jugador "
                            + idJugador
                            + " agregado al orden de juego."
            );


            // Asigna el primer turno
            if (
                    jugadorTurno == null
            ) {

                indiceTurno = 0;

                jugadorTurno =
                        ordenJugadores.get(0);


                generarNuevaPiezaJugador(
                        jugadorTurno
                );


                System.out.println(
                        "Turno inicial: Jugador "
                                + jugadorTurno
                );
            }
        }
    }


    // Consulta el tablero

    public String obtenerTableroComoTexto() {

        synchronized (lockJuego) {

            return tableroComoTextoConPiezas();
        }
    }


    // Procesa comandos

    public String procesarComando(
            int idJugador,
            String comando) {

        synchronized (lockJuego) {

            if (partidaFinalizada) {

                return "PARTIDA_FINALIZADA";
            }

            // Valida el turno
            if (
                    jugadorTurno == null
                    ||
                    jugadorTurno != idJugador
            ) {

                return "NO_ES_TU_TURNO - Turno del jugador "
                        + jugadorTurno;
            }


            Pieza pieza =
                    piezasJugadores.get(
                            idJugador
                    );


            if (
                    pieza == null
            ) {

                return "NO_HAY_PIEZA_ACTIVA";
            }


            switch (comando) {

                case "A":

                    moverIzquierda(
                            pieza
                    );

                    break;


                case "D":

                    moverDerecha(
                            pieza
                    );

                    break;


                case "S":

                    moverAbajo(
                            idJugador,
                            pieza
                    );

                    break;


                case "G1":

                    rotarAntihorario(
                            pieza
                    );

                    break;


                case "G2":

                    rotarHorario(
                            pieza
                    );

                    break;


                default:

                    return "COMANDO_NO_RECONOCIDO";
            }


            return "OK - Turno Jugador "
                    + jugadorTurno;
        }
    }


    // Mueve piezas

    private void moverIzquierda(
            Pieza pieza) {

        if (
                posicionValidaConOtrasPiezas(
                        pieza,
                        pieza.getFila(),
                        pieza.getColumna() - 1
                )
        ) {

            pieza.moverIzquierda();
        }
    }


    private void moverDerecha(
            Pieza pieza) {

        if (
                posicionValidaConOtrasPiezas(
                        pieza,
                        pieza.getFila(),
                        pieza.getColumna() + 1
                )
        ) {

            pieza.moverDerecha();
        }
    }


    private void moverAbajo(
            int idJugador,
            Pieza pieza) {

        if (
                posicionValidaConOtrasPiezas(
                        pieza,
                        pieza.getFila() + 1,
                        pieza.getColumna()
                )
        ) {

            pieza.moverAbajo();

        } else {

            fijarPiezaJugador(
                    idJugador,
                    pieza
            );
        }
    }


    // Rota piezas

    private void rotarHorario(
            Pieza pieza) {

        int[][] nuevaForma =
                pieza.rotarHorario();


        if (
                posicionValidaConOtrasPiezas(
                        pieza,
                        nuevaForma,
                        pieza.getFila(),
                        pieza.getColumna()
                )
        ) {

            pieza.aplicarForma(
                    nuevaForma
            );
        }
    }


    private void rotarAntihorario(
            Pieza pieza) {

        int[][] nuevaForma =
                pieza.rotarAntihorario();


        if (
                posicionValidaConOtrasPiezas(
                        pieza,
                        nuevaForma,
                        pieza.getFila(),
                        pieza.getColumna()
                )
        ) {

            pieza.aplicarForma(
                    nuevaForma
            );
        }
    }


    // Fija la pieza

    private void fijarPiezaJugador(
            int idJugador,
            Pieza pieza) {

        // Guarda la pieza en el tablero
        tablero.fijarPieza(
                pieza
        );


        // Quita la pieza activa
        piezasJugadores.remove(
                idJugador
        );


        // Elimina líneas completas
        int lineas =
                tablero.eliminarLineasCompletas();


        // Suma puntos por las líneas
        if (
                lineas > 0
        ) {

            int puntosGanados =
                    lineas * 100;


            int puntajeActual =
                    puntajes.getOrDefault(
                            idJugador,
                            0
                    );


            puntajeActual +=
                    puntosGanados;


            puntajes.put(
                    idJugador,
                    puntajeActual
            );


            System.out.println(
                    "Jugador "
                            + idJugador
                            + " completo "
                            + lineas
                            + " linea(s)."
            );


            System.out.println(
                    "Jugador "
                            + idJugador
                            + " gana "
                            + puntosGanados
                            + " puntos."
            );


            System.out.println(
                    "Puntaje total Jugador "
                            + idJugador
                            + ": "
                            + puntajeActual
            );
        }


        // Cambia el turno
        avanzarTurno();
    }


    // Consulta puntajes

    public int obtenerPuntajeJugador(
            int idJugador) {

        synchronized (lockJuego) {

            return puntajes.getOrDefault(
                    idJugador,
                    0
            );
        }
    }


    // Devuelve una copia de los puntajes
    public Map<Integer, Integer> obtenerCopiaPuntajes() {

        synchronized (lockJuego) {

            return new HashMap<Integer, Integer>(
                    puntajes
            );
        }
    }

    public boolean partidaTerminada() {

        synchronized (lockJuego) {

            return ordenJugadores.isEmpty();
        }
    }

    public boolean estaPartidaFinalizada() {

        synchronized (lockJuego) {

            return partidaFinalizada;
        }
    }

    public int obtenerGanador() {

        synchronized (lockJuego) {

            if (puntajes.isEmpty()) {

                return -1;
            }

            int idGanador = -1;
            int puntajeMaximo = Integer.MIN_VALUE;
            boolean empate = false;

            for (
                    Map.Entry<Integer, Integer> entrada
                            : puntajes.entrySet()
            ) {

                int puntaje =
                        entrada.getValue();

                if (puntaje > puntajeMaximo) {

                    puntajeMaximo = puntaje;
                    idGanador = entrada.getKey();
                    empate = false;

                } else if (puntaje == puntajeMaximo) {

                    empate = true;
                }
            }

            return empate ? -2 : idGanador;
        }
    }


    // Formatea los puntajes
    public String obtenerPuntajesComoTexto() {

        synchronized (lockJuego) {

            StringBuilder sb =
                    new StringBuilder();


            sb.append(
                    "===== PUNTAJES =====\n"
            );


            for (
                    Integer idJugador
                            : ordenJugadores
            ) {

                int puntos =
                        puntajes.getOrDefault(
                                idJugador,
                                0
                        );


                sb.append(
                        "Jugador "
                                + idJugador
                                + ": "
                                + puntos
                                + " puntos\n"
                );
            }


            sb.append(
                    "===================="
            );


            return sb.toString();
        }
    }


    // Genera piezas

    private void generarNuevaPiezaJugador(
            int idJugador) {

        while (true) {

            TipoPieza[] tipos =
                    TipoPieza.values();

            int indice =
                    (int) (
                            Math.random()
                                    * tipos.length
                    );

            TipoPieza tipo =
                    tipos[indice];

            // Asigna un símbolo al jugador.
            char simbolo =
                    (char) (
                            'A'
                                    + ((idJugador - 1) % 26)
                    );

            Pieza nuevaPieza =
                    new Pieza(
                            tipo,
                            simbolo
                    );

            nuevaPieza.setFila(
                    0
            );

            // Coloca la pieza cerca del centro
            int columnaInicial =
                    tablero.getColumnas()
                            / 2
                            - 2;

            // Separa las piezas por jugador
            columnaInicial +=
                    (idJugador - 1)
                            * 2;

            int ancho =
                    nuevaPieza
                            .getForma()[0]
                            .length;

            // Ajusta el borde derecho
            if (
                    columnaInicial + ancho
                            >= tablero.getColumnas()
            ) {

                columnaInicial =
                        tablero.getColumnas()
                                - ancho;
            }

            // Ajusta el borde izquierdo
            if (
                    columnaInicial < 0
            ) {

                columnaInicial = 0;
            }

            nuevaPieza.setColumna(
                    columnaInicial
            );

            // Comprueba si hay espacio
            if (
                    posicionValidaConOtrasPiezas(
                            nuevaPieza,
                            nuevaPieza.getFila(),
                            nuevaPieza.getColumna()
                    )
            ) {

                piezasJugadores.put(
                        idJugador,
                        nuevaPieza
                );

                System.out.println(
                        "Nueva pieza para jugador "
                                + idJugador
                                + ": "
                                + tipo
                                + " ["
                                + simbolo
                                + "]"
                );

                return;
            }

            piezasJugadores.remove(
                    idJugador
            );

            if (
                    jugadoresEliminados.add(
                            idJugador
                    )
            ) {

                gameOversPendientes.add(
                        idJugador
                );
            }

            boolean eraSuTurno =
                    jugadorTurno != null
                            && jugadorTurno == idJugador;

            int posicion =
                    ordenJugadores.indexOf(
                            idJugador
                    );

            if (
                    posicion >= 0
            ) {

                ordenJugadores.remove(
                        posicion
                );
            }

            System.out.println(
                    "Jugador "
                            + idJugador
                            + " sin espacio: GAME OVER."
            );

            if (
                    ordenJugadores.isEmpty()
            ) {

                partidaFinalizada = true;
                jugadorTurno = null;
                indiceTurno = 0;

                return;
            }

            if (eraSuTurno) {

                indiceTurno = posicion;

            } else if (
                    posicion >= 0
                            && posicion < indiceTurno
            ) {

                indiceTurno--;
            }

            if (
                    indiceTurno < 0
                            || indiceTurno >= ordenJugadores.size()
            ) {

                indiceTurno = 0;
            }

            jugadorTurno =
                    ordenJugadores.get(
                            indiceTurno
                    );

            if (
                    piezasJugadores.containsKey(
                            jugadorTurno
                    )
            ) {

                return;
            }

            idJugador =
                    jugadorTurno;
        }
    }

    public List<Integer> obtenerYLimpiarGameOversPendientes() {

        synchronized (lockJuego) {

            List<Integer> copia =
                    new ArrayList<Integer>(
                            gameOversPendientes
                    );

            gameOversPendientes.clear();

            return copia;
        }
    }


    // Valida posiciones

    private boolean posicionValidaConOtrasPiezas(
            Pieza piezaActual,
            int nuevaFila,
            int nuevaColumna) {

        return posicionValidaConOtrasPiezas(
                piezaActual,
                piezaActual.getForma(),
                nuevaFila,
                nuevaColumna
        );
    }


    private boolean posicionValidaConOtrasPiezas(
            Pieza piezaActual,
            int[][] forma,
            int nuevaFila,
            int nuevaColumna) {

        // Comprueba el tablero
        if (
                !tablero.posicionValida(
                        forma,
                        nuevaFila,
                        nuevaColumna
                )
        ) {

            return false;
        }


        // Comprueba otras piezas
        for (
                Map.Entry<Integer, Pieza> entrada
                        : piezasJugadores.entrySet()
        ) {

            Pieza otraPieza =
                    entrada.getValue();


            // Ignora la misma pieza
            if (
                    otraPieza
                            == piezaActual
            ) {

                continue;
            }


            if (
                    hayColisionEntrePiezas(
                            forma,
                            nuevaFila,
                            nuevaColumna,
                            otraPieza
                    )
            ) {

                return false;
            }
        }


        return true;
    }


    private boolean hayColisionEntrePiezas(
            int[][] forma1,
            int fila1,
            int columna1,
            Pieza pieza2) {

        int[][] forma2 =
                pieza2.getForma();


        for (
                int i = 0;
                i < forma1.length;
                i++
        ) {

            for (
                    int j = 0;
                    j < forma1[i].length;
                    j++
            ) {

                if (
                        forma1[i][j]
                                != 1
                ) {

                    continue;
                }


                int f1 =
                        fila1 + i;

                int c1 =
                        columna1 + j;


                for (
                        int x = 0;
                        x < forma2.length;
                        x++
                ) {

                    for (
                            int y = 0;
                            y < forma2[x].length;
                            y++
                    ) {

                        if (
                                forma2[x][y]
                                        != 1
                        ) {

                            continue;
                        }


                        int f2 =
                                pieza2.getFila()
                                        + x;

                        int c2 =
                                pieza2.getColumna()
                                        + y;


                        if (
                                f1 == f2
                                &&
                                c1 == c2
                        ) {

                            return true;
                        }
                    }
                }
            }
        }


        return false;
    }


    // Convierte el tablero a texto

    private String tableroComoTextoConPiezas() {

        char[][] copia =
                tablero.copiaCeldas();


        // Añade las piezas activas a una copia
        for (
                Pieza pieza
                        : piezasJugadores.values()
        ) {

            int[][] forma =
                    pieza.getForma();


            for (
                    int i = 0;
                    i < forma.length;
                    i++
            ) {

                for (
                        int j = 0;
                        j < forma[i].length;
                        j++
                ) {

                    if (
                            forma[i][j]
                                    == 1
                    ) {

                        int fila =
                                pieza.getFila()
                                        + i;

                        int columna =
                                pieza.getColumna()
                                        + j;


                        if (
                                fila >= 0
                                &&
                                fila < copia.length
                                &&
                                columna >= 0
                                &&
                                columna < copia[0].length
                        ) {

                            copia[fila][columna] =
                                    pieza.getSimbolo();
                        }
                    }
                }
            }
        }


        StringBuilder sb =
                new StringBuilder();


        for (
                int i = 0;
                i < copia.length;
                i++
        ) {

            sb.append(
                    "| "
            );


            for (
                    int j = 0;
                    j < copia[i].length;
                    j++
            ) {

                sb.append(
                        copia[i][j]
                );


                sb.append(
                        " "
                );
            }


            sb.append(
                    "|\n"
            );
        }


        return sb.toString();
    }


    // Avanza la caída automática

    public boolean avanzarCaidaAutomatica() {

        synchronized (lockJuego) {

            if (partidaFinalizada) {

                return false;
            }

            if (
                    jugadorTurno == null
            ) {

                return false;
            }


            Pieza pieza =
                    piezasJugadores.get(
                            jugadorTurno
                    );


            if (
                    pieza == null
            ) {

                return false;
            }


            if (
                    posicionValidaConOtrasPiezas(
                            pieza,
                            pieza.getFila() + 1,
                            pieza.getColumna()
                    )
            ) {

                pieza.moverAbajo();

            } else {

                // Guarda el jugador actual
                int jugadorActual =
                        jugadorTurno;


                fijarPiezaJugador(
                        jugadorActual,
                        pieza
                );
            }


            return true;
        }
    }


    // Consulta el turno

    public boolean esTurnoDe(
            int idJugador) {

        synchronized (lockJuego) {

            return jugadorTurno != null
                    &&
                    jugadorTurno == idJugador;
        }
    }


    public int getJugadorTurno() {

        synchronized (lockJuego) {

            if (
                    jugadorTurno == null
            ) {

                return -1;
            }


            return jugadorTurno;
        }
    }


    // Cambia el turno

    private void avanzarTurno() {

        if (
                ordenJugadores.isEmpty()
        ) {

            partidaFinalizada = true;
            jugadorTurno = null;

            indiceTurno = 0;

            return;
        }


        indiceTurno++;


        if (
                indiceTurno
                        >= ordenJugadores.size()
        ) {

            indiceTurno = 0;
        }


        jugadorTurno =
                ordenJugadores.get(
                        indiceTurno
                );


        System.out.println(
                "Nuevo turno: Jugador "
                        + jugadorTurno
        );


        generarNuevaPiezaJugador(
                jugadorTurno
        );
    }


    // Quita jugadores desconectados

    public void desregistrarJugador(
            int idJugador) {

        synchronized (lockJuego) {

            if (partidaFinalizada) {

                piezasJugadores.remove(
                        idJugador
                );

                return;
            }

            boolean eraSuTurno =
                    jugadorTurno != null
                    &&
                    jugadorTurno == idJugador;


            // Quita su pieza
            piezasJugadores.remove(
                    idJugador
            );


            int posicion =
                    ordenJugadores.indexOf(
                            idJugador
                    );


            // Quita al jugador activo
            if (
                    posicion != -1
            ) {

                ordenJugadores.remove(
                        Integer.valueOf(
                                idJugador
                        )
                );
            }


            System.out.println(
                    "Jugador "
                            + idJugador
                            + " eliminado del juego."
            );


            // Finaliza si no quedan jugadores
            if (
                    ordenJugadores.isEmpty()
            ) {

                partidaFinalizada = true;
                jugadorTurno = null;

                indiceTurno = 0;

                return;
            }


            // Ajusta el turno si hace falta
            if (
                    !eraSuTurno
            ) {

                if (
                        posicion >= 0
                        &&
                        posicion < indiceTurno
                ) {

                    indiceTurno--;
                }


                return;
            }


            // Pasa el turno al siguiente
            if (
                    posicion
                            >= ordenJugadores.size()
            ) {

                posicion = 0;
            }


            indiceTurno =
                    posicion;


            jugadorTurno =
                    ordenJugadores.get(
                            indiceTurno
                    );


            System.out.println(
                    "Nuevo turno por desconexion: Jugador "
                            + jugadorTurno
            );


            // Genera una pieza si hace falta
            if (
                    !piezasJugadores.containsKey(
                            jugadorTurno
                    )
            ) {

                generarNuevaPiezaJugador(
                        jugadorTurno
                );
            }
        }
    }
}