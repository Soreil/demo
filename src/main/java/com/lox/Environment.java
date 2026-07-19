package com.lox;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Environment {
    final Environment enclosing;

    public Environment() {
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    private final Map<String, Object> values = new HashMap<>();
    private final HashSet<String> uninitializedKeys = new HashSet<>();

    void defineInitialized(String name, Object value) {
        uninitializedKeys.remove(name);
        values.put(name, value);
    }

    void defineUninitialized(String name) {
        uninitializedKeys.add(name);
    }

    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (enclosing != null)
            return enclosing.get(name);

        if (uninitializedKeys.contains(name.lexeme)) {
            throw new RuntimeError(name, "Uninitialized variable '" + name.lexeme + "'.");
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    public void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        } else if (uninitializedKeys.contains(name.lexeme)) {
            uninitializedKeys.remove(name.lexeme);
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    public void define(String lexeme, Object object) {
        values.put(lexeme, object);
    }

    Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    void assignAt(int distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme, value);
    }

    Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }

        return environment;
    }

}
