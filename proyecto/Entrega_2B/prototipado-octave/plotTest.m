function plotTest(id_function, user="user_test", hide_plot=true)
  
  
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

  fclose(fid);


  x = 1:size(data_accelerometer_xs, 2);


  for i=1:size(ids, 1)
    close all;
    id = ids(i, 1);
    data_accelerometer_x = data_accelerometer_xs(i, :);
    data_accelerometer_y = data_accelerometer_ys(i, :);
    data_accelerometer_z = data_accelerometer_zs(i, :);
    data_gyroscope_x = data_gyroscope_xs(i, :);
    data_gyroscope_y = data_gyroscope_ys(i, :);
    data_gyroscope_z = data_gyroscope_zs(i, :);
    
    
    if(hide_plot)
      f = figure('visible','off');
    endif
    
    hold on;
    subplot(1, 2, 1)
    plot(x, data_accelerometer_x, 'r', 
         x, data_accelerometer_y, 'g', 
         x, data_accelerometer_z, 'b');

    legend ("Accelerometer X", "Accelerometer Y", "Accelerometer Z");

    subplot(1, 2, 2)
    plot(x, data_gyroscope_x, 'r', 
         x, data_gyroscope_y, 'g', 
         x, data_gyroscope_z, 'b');
         
    legend ("Gyroscope X", "Gyroscope Y", "Gyroscope Z");

    directory = strcat("plots/", strcat(user, strcat("/", strcat("function", num2str(id_function)))));
    [status, msg, msgID] = mkdir(directory);
    
    
    image_name = strcat(
      strcat(strcat(directory, "/"),gesture_name), 
      strcat(num2str(id), ".png")
    )
    print(image_name, "-dpng");
    

    
  endfor

endfunction

