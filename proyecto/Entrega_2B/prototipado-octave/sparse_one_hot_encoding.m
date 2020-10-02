function one_hot_encoding = sparse_one_hot_encoding(X, number_clases)
  % X - m x 1 vector
  % sparse_one_hot_encoding - m x max(X)
  one_hot_encoding = bsxfun(@eq, X(:), 1:number_clases);


  
endfunction
