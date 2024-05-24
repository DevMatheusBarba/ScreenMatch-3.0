package br.com.alura.screenmatch.model;

public enum Categoria {

    ACAO("Action", "ação"),
    COMEDIA("Comedy", "comédia"),
    DRAMA("Drama", "drama"),
    CRIME("Crime", "crime"),
    ROMANCE("Romance", "romance");

    private String categoriaOmdb;

    private String categoriaPortugues;

    Categoria (String categoriaOMDB, String categoriaPortugues){
        this.categoriaOmdb = categoriaOMDB;
        this.categoriaPortugues = categoriaPortugues;
    }

    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }

    public static Categoria fromStringPortugues(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaPortugues.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }

}
