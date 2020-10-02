function [X y] = getData(percent_augmentation=0.1, ...
                         samples=-1, parts_for_gesture=100, ... 
                         normalize=true, init_forecast=10, limit_forecast=30)
  
  
id_functions = [15, 16, 17];
labels = [1, 2, 3];

X = [];
y = [];


counter_data_augmented_by_shift = 0;
counter_data_augmented_by_move_average = 0;

X_augmentation = [];
y_augmentation = [];

user = 'user_test';


index_begin = 1;
index_end = 0;

index_augmentation_begin = 1;

for i=1:size(id_functions, 2)
    
  id_function = id_functions(i);
  
  filename = strcat(
    strcat("data,", 
      strcat(user, ",function")
    ), num2str(id_function)
  );
  extention = ".txt";
  fid = fopen(strcat("data/", strcat(filename, extention)));


  gesture_name = "gesture_uid_";



  ids = str2num(fgetl(fid));
  data_accelerometer_xs = str2num(fgetl(fid));
  data_accelerometer_ys = str2num(fgetl(fid));
  data_accelerometer_zs = str2num(fgetl(fid));
  data_gyroscope_xs = str2num(fgetl(fid));
  data_gyroscope_ys = str2num(fgetl(fid));
  data_gyroscope_zs = str2num(fgetl(fid));
  
  if normalize
    data_accelerometer_xs = (data_accelerometer_xs - mean(data_accelerometer_xs(:)))/std(data_accelerometer_xs(:));
    data_accelerometer_ys = (data_accelerometer_ys - mean(data_accelerometer_ys(:)))/std(data_accelerometer_ys(:));
    data_accelerometer_zs = (data_accelerometer_zs - mean(data_accelerometer_zs(:)))/std(data_accelerometer_zs(:));
    data_gyroscope_xs = (data_gyroscope_xs - mean(data_gyroscope_xs(:)))/std(data_gyroscope_xs(:));
    data_gyroscope_ys = (data_gyroscope_ys - mean(data_gyroscope_ys(:)))/std(data_gyroscope_ys(:));
    data_gyroscope_zs = (data_gyroscope_zs - mean(data_gyroscope_zs(:)))/std(data_gyroscope_zs(:));
    
  endif
  
  
  
  
  m = size(data_accelerometer_xs,1);

  fclose(fid);
  
  index_end += m;
  
  label = labels(i);
  
  X(index_begin:index_end, :) = [data_accelerometer_xs, data_accelerometer_ys, data_accelerometer_zs, data_gyroscope_xs, data_gyroscope_ys, data_gyroscope_zs];
  y(index_begin:index_end, 1) = label;
  
  index_begin += m;
  
  threshold_augmentation = floor(parts_for_gesture * percent_augmentation);
  
    
    
  for index_sample=1:m  % por cada ejemplo
    
    
  
    original_data_accelerometer_x =  data_accelerometer_xs(index_sample, :); % 1x100    
    original_data_accelerometer_y =  data_accelerometer_ys(index_sample, :); % 1x100    
    original_data_accelerometer_z =  data_accelerometer_zs(index_sample, :); % 1x100    
    original_data_gyroscope_x =  data_gyroscope_xs(index_sample, :); % 1x100    
    original_data_gyroscope_y =  data_gyroscope_ys(index_sample, :); % 1x100    
    original_data_gyroscope_z =  data_gyroscope_zs(index_sample, :); % 1x100    
    
    
    
    
    for shift=1:threshold_augmentation
      augmented_data_accelerometer_x_left = shiftVector(original_data_accelerometer_x(:)', shift, 0);  % transpuesta conserva las dimensiones de X en Octave
      augmented_data_accelerometer_x_right = shiftVector(original_data_accelerometer_x(:)', shift, 1);  % transpuesta conserva las dimensiones de X en Octave

      augmented_data_accelerometer_y_left = shiftVector(original_data_accelerometer_y(:)', shift, 0);  % transpuesta conserva las dimensiones de X en Octave
      augmented_data_accelerometer_y_right = shiftVector(original_data_accelerometer_y(:)', shift, 1);  % transpuesta conserva las dimensiones de X en Octave

      augmented_data_accelerometer_z_left = shiftVector(original_data_accelerometer_z(:)', shift, 0);  % transpuesta conserva las dimensiones de X en Octave
      augmented_data_accelerometer_z_right = shiftVector(original_data_accelerometer_z(:)', shift, 1);  % transpuesta conserva las dimensiones de X en Octave

      augmented_data_gyroscope_x_left = shiftVector(original_data_gyroscope_x(:)', shift, 0);  % transpuesta conserva las dimensiones de X en Octave
      augmented_data_gyroscope_x_right = shiftVector(original_data_gyroscope_x(:)', shift, 1);  % transpuesta conserva las dimensiones de X en Octave

      augmented_data_gyroscope_y_left = shiftVector(original_data_gyroscope_y(:)', shift, 0);  % transpuesta conserva las dimensiones de X en Octave
      augmented_data_gyroscope_y_right = shiftVector(original_data_gyroscope_y(:)', shift, 1);  % transpuesta conserva las dimensiones de X en Octave

      augmented_data_gyroscope_z_left = shiftVector(original_data_gyroscope_z(:)', shift, 0);  % transpuesta conserva las dimensiones de X en Octave
      augmented_data_gyroscope_z_right = shiftVector(original_data_gyroscope_z(:)', shift, 1);  % transpuesta conserva las dimensiones de X en Octave

      
      X_augmentation(index_augmentation_begin, :) = [augmented_data_accelerometer_x_left, augmented_data_accelerometer_y_left, augmented_data_accelerometer_z_left, augmented_data_gyroscope_x_left, augmented_data_gyroscope_y_left, augmented_data_gyroscope_z_left];
      X_augmentation(index_augmentation_begin+1, :) = [augmented_data_accelerometer_x_right, augmented_data_accelerometer_y_right, augmented_data_accelerometer_z_right, augmented_data_gyroscope_x_right, augmented_data_gyroscope_y_right, augmented_data_gyroscope_z_right];
      y_augmentation(index_augmentation_begin, 1) = label;
      y_augmentation(index_augmentation_begin+1, 1) = label;
      
      index_augmentation_begin += 2;
    endfor
    
    
    for forecast=init_forecast:limit_forecast
      
      X_augmentation(index_augmentation_begin, :) = [ ...
        moving_average_box(original_data_accelerometer_x, forecast), ...
        moving_average_box(original_data_accelerometer_y, forecast), ...
        moving_average_box(original_data_accelerometer_z, forecast), ...
        moving_average_box(original_data_gyroscope_x, forecast), ...
        moving_average_box(original_data_gyroscope_y, forecast), ...
        moving_average_box(original_data_gyroscope_z, forecast)
        
      ];
      y_augmentation(index_augmentation_begin, 1) = label;
      index_augmentation_begin += 1;
    endfor
    
    

    
  endfor
  
  
  counter_data_augmented_by_shift += 2 * m * threshold_augmentation;
  counter_data_augmented_by_move_average += (limit_forecast-init_forecast+1) * m;
    
  
  
  
  X = [X; X_augmentation];
  y = [y; y_augmentation];


  
  m = size(X, 1);
  idx = randperm(m);
  
  X_shuffled = X(idx, :);
  y_shuffled = y(idx, :);
  
  if(samples < 1)
    X = X_shuffled;
    y = y_shuffled;
  elseif(samples >= 1)
    X = X_shuffled(1:samples,:);
    y = y_shuffled(1:samples,:);

  endif

endfor


counter_data_augmented_by_shift
counter_data_augmented_by_move_average
    
  
  
endfunction
