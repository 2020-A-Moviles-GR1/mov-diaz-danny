clear all;

lambda = 0;  % si es muy alto provoca underfiting por Hipotesis = 0
% todo: comprobar que la regularización se calcula correctamente
alpha = 0.1;
epochs = 5;
neurons_per_layer = [-1, 35, 18, -1];  % 42

[X y] = getData(0.1, -1, 100, false, 10, 30);% getData(0.1, -1, 100, true, 10, 30); % getToyData();

% X (examplesxfeatures
% y (examplesx1)
verbose = true;

% 600 features, 3 diferent output




[X_train y_train X_test y_test X_validation y_validation] = split_train_test_validation(X, y, 0.6, 0.2);

m = size(X, 1); % número de ejemplos

number_features = size(X, 2);  % número de caracteristicas, en este caso son 600
number_clases = size(unique(y), 1);  % número de clases
neurons_per_layer(1) = number_features;
neurons_per_layer(end) = number_clases;

neurons_per_layer

% num_weightsx1
thetas_rolled = initWeights(neurons_per_layer);


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%5



[new_thetas_rolled Js_train Js_validation] = train(neurons_per_layer, thetas_rolled, epochs, X_train, y_train, X_validation, y_validation, alpha, lambda, verbose);

J_test = nnCostFunction(neurons_per_layer, new_thetas_rolled, X_test, y_test, lambda);
disp(sprintf('Error final de test: %.2f', J_test));

hold on;
plot(1:size(Js_train, 2), Js_train, 'b');
plot(1:size(Js_validation, 2), Js_validation, 'r');
xlabel('iterations')
ylabel('Cost')
legend('training', 'validation')
title('Cost by iteration')

predictions_train = predict(neurons_per_layer, new_thetas_rolled, X_train);
predictions_test = predict(neurons_per_layer, new_thetas_rolled, X_test);

fprintf('\nTraining Set Accuracy: %f\n', mean(double(predictions_train == y_train)) * 100);
fprintf('\nTest Set Accuracy: %f\n', mean(double(predictions_test == y_test)) * 100);




%{






[Js_train Js_validation] = learningCurves(neurons_per_layer, thetas_rolled, X_train, y_train, X_validation, y_validation, alpha, lambda, epochs);

hold on;
plot(1:size(Js_train, 2), Js_train, 'b');
plot(1:size(Js_validation, 2), Js_validation, 'r');
xlabel('m')
ylabel('J')
legend('training', 'validation')
title('Learning Curves')






%}
