function new_vector = shiftVector(vector, positions, direction = 0)
  
  vector_length = size(vector, 2);
  
  if direction == 0  % izquierda
    new_vector = [zeros(1, positions) vector(1:vector_length-positions)];
  elseif direction == 1
    new_vector = [vector(positions+1:vector_length) zeros(1, positions)];
  end
  
  
endfunction
