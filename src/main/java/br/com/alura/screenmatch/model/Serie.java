package br.com.alura.screenmatch.model;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table (name = "series")
public class Serie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titulo;
    private Integer totalTemporadas;
    private Double avaliacao;
    private String escritor;
    @Enumerated(EnumType.STRING)
    private Categoria genero;
    private String atores;
    private String poster;
    private String sinopse;

    @OneToMany(mappedBy = "serie" , cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Episodio> episodios = new ArrayList<>();


    public Serie(){}

    public Serie(DadosSerie dadosSerie) {
        this.titulo = dadosSerie.titulo();
        this.totalTemporadas = dadosSerie.totalTemporadas();
        this.avaliacao = OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).orElse(0);
        this.escritor = dadosSerie.escritor();
        this.genero = Categoria.fromString(dadosSerie.genero().split(",")[0].trim());
        this.atores = dadosSerie.atores();
        this.poster = dadosSerie.poster();
        this.sinopse = dadosSerie.sinopse().trim();
    }

    public List<Episodio> getEpisodios() {
        return episodios;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public String getEscritor() {
        return escritor;
    }

    public Categoria getGenero() {
        return genero;
    }

    public String getAtores() {
        return atores;
    }

    public String getPoster() {
        return poster;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public void setEscritor(String escritor) {
        this.escritor = escritor;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public void setAtores(String atores) {
        this.atores = atores;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public void setEpisodios(List<Episodio> episodios) {
        episodios.forEach(e -> e.setSerie(this));
        this.episodios = episodios;
    }

    @Override
    public String toString() {
        return
                "genero=" + genero +
                        ", titulo='" + titulo + '\'' +
                        ", totalTemporadas=" + totalTemporadas +
                        ", avaliacao=" + avaliacao +
                        ", escritor='" + escritor + '\'' +

                        ", atores=" + atores +
                        ", poster='" + poster + '\'' +
                        ", sinopse='" + sinopse + '\'' +
                        ", Episodio ='" + episodios + '\'';
    }
}
