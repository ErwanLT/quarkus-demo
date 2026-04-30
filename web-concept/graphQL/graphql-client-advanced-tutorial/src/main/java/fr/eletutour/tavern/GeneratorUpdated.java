package fr.eletutour.tavern;

import io.smallrye.graphql.client.generator.Generator;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collector;
import java.util.stream.Stream;

import graphql.language.Argument;
import graphql.language.Document;
import graphql.language.Field;
import graphql.language.FieldDefinition;
import graphql.language.ListType;
import graphql.language.NonNullType;
import graphql.language.ObjectTypeDefinition;
import graphql.language.OperationDefinition;
import graphql.language.SelectionSet;
import graphql.language.Type;
import graphql.language.TypeName;
import graphql.language.Value;
import graphql.language.VariableDefinition;
import graphql.language.VariableReference;
import graphql.parser.Parser;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

public class GeneratorUpdated extends Generator {

    public GeneratorUpdated(String pkg, String apiTypeName, String schema, List<String> queries) {
        super(pkg, apiTypeName, schema, queries);
    }

    private List<String> extractFields(Field field) {
        List<String> result = new ArrayList<>();

        if (field.getSelectionSet() != null) {
            for (Field sub : field.getSelectionSet().getSelectionsOfType(Field.class)) {
                result.add(sub.getName());

                // récursion pour nested types
                if (sub.getSelectionSet() != null) {
                    result.addAll(extractFields(sub));
                }
            }
        }

        return result;
    }
}
