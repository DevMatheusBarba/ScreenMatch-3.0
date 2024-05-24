package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

import static br.com.alura.screenmatch.model.Categoria.fromStringPortugues;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = System.getenv("APIKEYOMDB");
    private List<DadosSerie> dadosSeries = new ArrayList<>();

    private List<Serie> series = new ArrayList<>();

    private SerieRepository repositorio;

    public Principal(SerieRepository repository) {
        this.repositorio = repository;
    }

    public void exibeMenu() {


        int opcao;
        var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar séries
                4 - Buscar serie por titulo
                5 - Buscar series por atore
                6 - Top 7 series
                7 - Buscar por Genêro
                8 - Buscar por um numero maximo de temporada
                                
                0 - Sair                                 
                """;
        do {
            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarDadosSeries();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriesPorAtor();
                    break;
                case 6:
                    buscarTop7Series();
                    break;
                case 7:
                    buscarSeriesPorGenero();
                    break;
                case 8:
                    buscarPorTemporadas();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        } while (opcao != 0);
    }


    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie dadosSerie = new Serie(dados);
        // dadosSeries.add(dados);
        repositorio.save(dadosSerie);
        System.out.println("\n" + dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obtemDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarDadosSeries();
        System.out.println("Busque os episódios pelo nome da serie");
        var serieBuscada = leitura.nextLine();
        List<DadosTemporada> temporadas = new ArrayList<>();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(serieBuscada))
                .findFirst();

        if (serie.isPresent()) {

            var serieEncontrada = serie.get();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obtemDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);

        } else {
            System.out.println("Serie não localizada");
        }
    }

    private void listarDadosSeries() {
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma serie pelo nome:");
        var nomeSerie = leitura.nextLine();

        var serieBuscada = repositorio.findByTituloContainsIgnoreCase(nomeSerie);


        if (serieBuscada.isPresent()) {
            System.out.println("Dados da serie: " + serieBuscada.get());
        } else {
            System.out.println("Serie não listada no banco de dados");
        }

    }

    private void buscarSeriesPorAtor() {
        System.out.println("Digite o nome do ator que deseja: ");
        var atorBuscado = leitura.nextLine();
        System.out.println("Qual a avalição minima");
        var avalicaoMinima = leitura.nextDouble();
        List<Serie> serieEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(atorBuscado, avalicaoMinima);
        if (serieEncontradas.size() > 0) {
            System.out.println("Series em que " + atorBuscado + " participou");
            serieEncontradas.forEach(s -> {
                System.out.println(s.getTitulo() + " Avaliação: " + s.getAvaliacao());
            });
        } else {
            System.out.println("Ator não encontrado no banco");
        }

    }


    private void buscarTop7Series() {
        List<Serie> top7Series = repositorio.findTop7ByOrderByAvaliacaoDesc();

        top7Series.forEach(s -> {
            System.out.println(s.getTitulo() + " Avaliação: " + s.getAvaliacao());
        });
    }

    private void buscarSeriesPorGenero() {
        System.out.println("Digite o genêro que deseja buscar");
        var  generoBuscado = leitura.nextLine();
        Categoria categoria = fromStringPortugues(generoBuscado);
        List<Serie> seriesBuscadas = repositorio.findByGenero(categoria);
        seriesBuscadas.forEach(s ->
                System.out.println("Serie: " + s.getTitulo() + " Genêro: " + s.getGenero() ));
    }

    private void buscarPorTemporadas() {
        System.out.println("Quantidade máxima de temporada");
        var maximaTemporada = leitura.nextInt();
        System.out.println("Qual a avaliação minima");
        var minimaAvaliacao = leitura.nextDouble();

        List<Serie> seriesBuscadas = repositorio.findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(maximaTemporada, minimaAvaliacao);
        seriesBuscadas.forEach(s ->
                System.out.println("Serie: " + s.getTitulo() + " Temporadas: " + s.getTotalTemporadas() ));

    }



}