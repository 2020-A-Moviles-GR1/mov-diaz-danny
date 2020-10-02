function g = sigmoid(z)
  g = 1.0 ./ (1+exp(-z));
endfunction
