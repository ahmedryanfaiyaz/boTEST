package configuration.generators;

import configuration.pojos.*;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import main.OpenApiSpecification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DefaultTestConfigurationGenerator {
    private OpenApiSpecification openApiSpecification;

    public DefaultTestConfigurationGenerator(OpenApiSpecification openApiSpecification) {
        this.openApiSpecification = openApiSpecification;
    }

    public TestConfigurationObject generate() {
        TestConfigurationObject testConfigurationObject = new TestConfigurationObject();

        // generate Auth
        generateAuth(testConfigurationObject);

        // generate Test Configuration
        generateTestConfiguration(testConfigurationObject);

        System.out.println(testConfigurationObject.toString());

        return testConfigurationObject;
    }

    private void generateTestConfiguration(TestConfigurationObject testConfigurationObject) {
        TestConfiguration testConfiguration = new TestConfiguration();

        testConfigurationObject.setTestConfiguration(testConfiguration);
        // generate operations
        generateOperations(testConfiguration);
    }

    private void generateOperations(TestConfiguration testConfiguration) {
        List<Operation> operations = new ArrayList<>();

        testConfiguration.setOperations(operations);

        for (Map.Entry<String, PathItem> path: openApiSpecification.getSpecification().getPaths().entrySet()) {
            // GET
            if (path.getValue().getGet() != null) {
                Operation operation = new Operation();
                operation.setTestPath(path.getKey());
                operation.setMethod("GET");
                operation.setOperationId(path.getValue().getGet().getOperationId());

                generateParameters(path.getValue().getGet().getParameters(), operation);
//                operation.setTestParameters();
//                operation.setExpectedResponse();

                operations.add(operation);
            }

            // operation.setOperationId(path.getValue().);

            // TODO: POST
            // TODO: PUT
            // TODO: PATCH
            // TODO: OPTIONS
            // TODO: HEAD
            // TODO: TRACE
            // TODO: DELETE
        }
    }

    private void generateParameters(List<Parameter> parameters, Operation operation) {
        List<TestParameter> testParameters = new ArrayList<>();

        for (Parameter parameter: parameters) {
            TestParameter testParameter = new TestParameter();

            testParameter.setName(parameter.getName());
            testParameter.setIn(parameter.getIn());
            testParameter.setWeight(0.5); // parameter is optional

            if (parameter.getRequired() != null) {
                if (parameter.getRequired()) {
                    testParameter.setWeight(1); // parameter is required
                }
            }

            generateGenerators(testParameter, parameter.getSchema());

            testParameters.add(testParameter);
        }

        operation.setTestParameters(testParameters);
    }

    private void generateGenerators(TestParameter testParameter, Schema schema) {
        List<Generator> generators = new ArrayList<>();

        Generator generator = new Generator();
        generator.setValid(true);

        if (schema.getType().equals("boolean")) {
            generator.setType("RandomBooleanGenerator");
        } else if (schema.getType().equals("integer")) {
            generator.setType("RandomIntegerGenerator");
        } else if (schema.getType().equals("string")) {
            generator.setType("RandomStringGenerator");
        }

        generateGenParameters(generator, schema);

        generators.add(generator);

        testParameter.setGenerators(generators);
    }

    private void generateGenParameters(Generator generator, Schema schema) {
        List<GenParameter> genParameters = new ArrayList<>();

        if (schema.getType().equals("boolean")) {
            // gen parameters will be empty list
        }
        else if (schema.getType().equals("string")) {

            /**
             * Min Length Parameter Values
             */
            GenParameter genParameterMinLength = new GenParameter();

            genParameterMinLength.setName("minLength");
            ArrayList<String> minLengthValues = new ArrayList<>();

            if (schema.getMinLength() != null) {
                minLengthValues.add(schema.getMinLength().toString());
                genParameterMinLength.setValues(minLengthValues);
            }

            genParameters.add(genParameterMinLength);

            /**
             * Max Length Parameter Values
             */
            GenParameter genParameterMaxLength = new GenParameter();

            genParameterMaxLength.setName("maxLength");
            ArrayList<String> maxLengthValues = new ArrayList<>();

            if (schema.getMaxLength() != null) {
                maxLengthValues.add(schema.getMaxLength().toString());
                genParameterMaxLength.setValues(maxLengthValues);
            }

            genParameters.add(genParameterMaxLength);

            // TODO: include alphabetic
            // TODO: include numbers
            // TODO: include special char

            generator.setGenParameters(genParameters);
        }
        else if (schema.getType().equals("integer")) {

            /**
             * Data Type Values
             */

            GenParameter genParameterType = new GenParameter();

            genParameterType.setName("type");
            ArrayList<String> typeValues = new ArrayList<>();

            // if (schema.getType() != null) {
            //     typeValues.add(schema.getType());
            // }

            typeValues.add(schema.getType());
            genParameterType.setValues(typeValues);

            genParameters.add(genParameterType);


            /**
             * Min Parameter Values
             */

            GenParameter genParameterMinNumber = new GenParameter();

            genParameterMinNumber.setName("min");
            ArrayList<String> minValues = new ArrayList<>();

            if (schema.getMinimum() != null) {
                minValues.add(schema.getMinimum().toString());
                genParameterMinNumber.setValues(minValues);
            }

            genParameters.add(genParameterMinNumber);


            /**
             * Max Parameter Values
             */

            GenParameter genParameterMaxNumber = new GenParameter();

            genParameterMaxNumber.setName("max");
            ArrayList<String> maxValues = new ArrayList<>();

            if (schema.getMaximum() != null) {
                maxValues.add(schema.getMaximum().toString());
                genParameterMaxNumber.setValues(maxValues);
            }

            genParameters.add(genParameterMaxNumber);
        }

        generator.setGenParameters(genParameters);
    }

    private void generateAuth(TestConfigurationObject testConfigurationObject) {
        Auth auth = new Auth();

        // process auth
        openApiSpecification.getSpecification().getSecurity();
        testConfigurationObject.setAuth(new Auth());

        testConfigurationObject.setAuth(auth);
    }
}