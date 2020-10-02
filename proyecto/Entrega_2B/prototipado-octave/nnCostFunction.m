function J = nnCostFunction(neurons_per_layer, thetas_rolled, X, y, lambda)
  
  m = size(X, 1);  % número de ejemplos
  number_layers = size(neurons_per_layer, 2);  % número de capas 1x4
  
  
  
  %% Feed Forward
  index_reshape_begin = 1;
  index_reshape_end = 0;
  
  
  % a = mx(features+1) el +1 es el bias -> neurons_per_layer(i)+1 = features+1 en esta asignación
  a = [ones(m, 1) X];
  
  
  regularization_sum = 0;
  
  
  for i=1:number_layers-1
    index_reshape_end += (neurons_per_layer(i)+1)*neurons_per_layer(i+1);
    
    % theta = (neurons_per_layer(i)+1)x(neurons_per_layer(i+1))
    theta = reshape(thetas_rolled(index_reshape_begin:index_reshape_end), (neurons_per_layer(i)+1), neurons_per_layer(i+1));
    
    % reshape test
    % a =  1    2    3    4    5    6    7    8    9   10   11   12   13   14   15   16
    % reshape(a, 8, 2) =
    %1    9
    %2   10
    %..  ..
    %7   15
    %8   16
    
    regularization_sum += sum( sum( theta(2:end, :) .^ 2 ) );
    
    % z = mx(neurons_per_layer(i)+1) * (neurons_per_layer(i)+1)x(neurons_per_layer(i+1)) 
    % = mxneurons_per_layer(i+1)
    z = a * theta;
    % a = mx(neurons_per_layer(i+1)+1)
    a = [ones(m, 1) sigmoid(z)];
    index_reshape_begin = index_reshape_end + 1;
  end
  
  % hipotesis es a, la respuesta correcta es y (hot encoded)
  sparse_y = sparse_one_hot_encoding(y, neurons_per_layer(number_layers));
  % [1 0 0] si y es 1 y hay 3 clases
  %a_w_b = a(:, 2:end)
  %spneg = -sparse_y
  %lg = log(a(:, 2:end))
  %mult = -sparse_y .* log(a(:, 2:end))
  
  regularization = (lambda/(2*m)) * regularization_sum;

  J = sum(sum( -sparse_y .* log(a(:, 2:end)) - (1 - sparse_y) .* log(1 - a(:, 2:end))))/m + regularization;
  
    
 endfunction
