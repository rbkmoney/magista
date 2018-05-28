package com.rbkmoney.magista.dao.binding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.util.Optional;

public class JsonBinder implements Binding<Object, JsonNode> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Converter<Object, JsonNode> converter() {
        return new Converter<Object, JsonNode>() {

            @Override
            public JsonNode from(Object databaseObject) {
                return Optional.ofNullable(databaseObject)
                        .map(string -> {
                            try {
                                return objectMapper.readTree(databaseObject.toString());
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        })
                        .orElse(null);
            }

            @Override
            public String to(JsonNode userObject) {
                return Optional.ofNullable(userObject)
                        .map(node -> {
                            try {
                                return objectMapper.writeValueAsString(node);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        })
                        .orElse(null);
            }

            @Override
            public Class<Object> fromType() {
                return Object.class;
            }

            @Override
            public Class<JsonNode> toType() {
                return JsonNode.class;
            }
        };
    }

    @Override
    public void sql(BindingSQLContext<JsonNode> ctx) throws SQLException {
        ctx.render().visit(DSL.val(ctx.convert(converter()).value())).sql("::jsonb");
    }

    @Override
    public void register(BindingRegisterContext<JsonNode> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR);
    }

    @Override
    public void set(BindingSetStatementContext<JsonNode> ctx) throws SQLException {
        ctx.statement().setString(ctx.index(), ctx.convert(converter()).value().toString());
    }

    @Override
    public void set(BindingSetSQLOutputContext<JsonNode> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void get(BindingGetResultSetContext<JsonNode> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()));
    }

    @Override
    public void get(BindingGetStatementContext<JsonNode> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.statement().getString(ctx.index()));
    }

    @Override
    public void get(BindingGetSQLInputContext<JsonNode> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
