function new_vector = moving_average_box(vector, forecast_moving = 3)
  
  len = size(vector, 2);
  new_vector = zeros(size(vector));
  
  step = ceil(forecast_moving/2);
  
  for i=step:len-step+1
    new_vector(i) = sum(vector(i-step+1:i+step-1))/forecast_moving;
  endfor
  
endfunction
