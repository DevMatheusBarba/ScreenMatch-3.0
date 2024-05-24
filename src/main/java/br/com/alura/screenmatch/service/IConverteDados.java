package br.com.alura.screenmatch.service;

public interface IConverteDados {
    <T> T obtemDados(String json, Class<T> classe);
}
