package K_means;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.lang.String;


public class Main {

    static final int COUNT_ELEMENTS = 60000;                       /* Заданное колличество элементов */
    static final int COUNT_ATTRIBUTES = 11;                        /* Заданное колличество атрибутов */
    static final int COUNT_CLUSTERS = 12;                          /* Заданное колличество кластеров */
    static final int COUNT_CYCLES = 100;

    static Element[] arrElements = new Element[COUNT_ELEMENTS];
    static Element[] centroids = new Element[COUNT_CLUSTERS];
    static ArrayList[] clusters = new ArrayList[COUNT_CLUSTERS];
    static Element[] centroidsTemp = new Element[COUNT_CLUSTERS];

    public static void main(String[] args) throws Exception {

        long startTime = System.currentTimeMillis();
        {

            for (int i = 0; i < COUNT_ELEMENTS; i++) {
                arrElements[i] = new Element();
            }

            //// Обработка данных (заполнение массива arrElements) ////

            File data = new File("D:\\data.arff");
            FileReader fr = new FileReader(data);
            Scanner scan = new Scanner(fr);

            String str;                                           /* Строка для хранения атрибута элемента */

            StringBuilder buffer = new StringBuilder();

            for (int i = 0; i < COUNT_ELEMENTS; i++) {

                str = scan.nextLine();

                int j = 0;
                for (int k = 0; k < str.length(); k++) {
                    if (str.charAt(k) != ',') {
                        buffer.append(str.charAt(k));
                    }
                    if (str.charAt(k) == ',' || k == str.length() - 1) {
                        arrElements[i].attribute[j] = Double.parseDouble(buffer.toString());
                        j++;
                        buffer = new StringBuilder();  //очистка буфера
                    }
                }
            }

            fr.close();

            //// Создание начальных кластеров ////

            for (int i = 0; i < COUNT_CLUSTERS; i++) {
                centroids[i] = new Element();
            }

            for (int i = 0; i < COUNT_CLUSTERS; i++) {
                clusters[i] = new ArrayList<Element>();
            }

            //// Определение начальных центроидов ////

            System.out.println("\nНачальные центроиды:");

            for (int i = 0; i < COUNT_CLUSTERS; i++) {
                for (int k = 0; k < COUNT_ATTRIBUTES; k++) {
                    centroids[i].attribute[k] = arrElements[i].attribute[k];

                    //// Вывод начальных центроидов ////

                    if (k == 0)
                        System.out.print(i + 1 + " кластер: ");
                    if (k != COUNT_ATTRIBUTES - 1)
                        System.out.print(centroids[i].attribute[k] + ", ");
                    else
                        System.out.println();
                }
            }

            //// Создание временных центроидов ////


            for (int i = 0; i < COUNT_CLUSTERS; i++) {
                centroidsTemp[i] = new Element();
            }

            //// Заполнение кластеров ////

            double distance;
            double distanceMin = 0;
            int numCluster;

            int c;

            for (c = 0; c < COUNT_CYCLES; c++) {

                for (int i = 0; i < COUNT_ELEMENTS; i++) {

                    numCluster = 0;

                    for (int n = 0; n < COUNT_CLUSTERS; n++) {
                        distance = 0;
                        for (int k = 0; k < COUNT_ATTRIBUTES; k++) {
                            distance += Math.pow(arrElements[i].attribute[k] - centroids[n].attribute[k], 2);
                        }
                        distance = Math.sqrt(distance);
                        if (n == 0) {
                            distanceMin = distance;
                            numCluster = n;
                        }
                        if (distance < distanceMin) {
                            distanceMin = distance;
                            numCluster = n;
                        }
                    }
                    clusters[numCluster].add(arrElements[i]);
                }

                //// Пересчет центоидов ////


                Thread2 thread = new Thread2();
                thread.start();

                for (int i = 0; i < COUNT_CLUSTERS; i+=2) {

//                    new Thread3("thread " + i, i).start();

                  double res;
                    int clusterSize = clusters[i].size();
                    for (int n = 0; n < COUNT_ATTRIBUTES; n++) {
                        res = 0;
                        for (int k = 0; k < clusterSize; k++) {
                            res += ((Element) clusters[i].get(k)).attribute[n];
                        }
                        res /= clusterSize;
                        centroidsTemp[i].attribute[n] = centroids[i].attribute[n];
                        centroids[i].attribute[n] = res;
                    }
                }

                thread.join();


                //// Проверка центроидов ////

                boolean flag = false;
                int tmp1;
                int tmp2 = 0;
                for (int i = 0; i < COUNT_CLUSTERS; i++) {
                    tmp1 = 0;
                    for (int n = 0; n < COUNT_ATTRIBUTES; n++) {
                        if (centroids[i].attribute[n] == centroidsTemp[i].attribute[n])
                            tmp1++;
                        else break;
                        if (tmp1 == COUNT_ATTRIBUTES)
                            tmp2++;
                        if (tmp2 == COUNT_CLUSTERS)
                            flag = true;
                    }
                }

                if (flag)
                    break;

                //// Очистка кластеров ////

                if (c != COUNT_CYCLES - 1) {
                    for (int i = 0; i < COUNT_CLUSTERS; i++) {
                        clusters[i].clear();
                    }
                }
            }

            //// Вывод конечных центроидов ////

            System.out.println("\nКонечные центроиды:");

            for (int i = 0; i < COUNT_CLUSTERS; i++) {
                for (int k = 0; k < COUNT_ATTRIBUTES; k++) {
                    if (k == 0)
                        System.out.print(i + 1 + " кластер: ");
                    if (k != COUNT_ATTRIBUTES - 1)
                        System.out.print(centroids[i].attribute[k] + ", ");
                    else
                        System.out.println();
                }
            }

            //// Вывод размера конечных кластеров ////

            System.out.println("\nКонечный размер кластеров:");

            for (int i = 0; i < COUNT_CLUSTERS; i++) {
                System.out.println(i + 1 + " кластер: " + clusters[i].size());
            }

            System.out.print("\nЦиклов пройдено: " + c + '\n');

        }

        System.out.println("Время выполнения программы: " + (System.currentTimeMillis() - startTime) + " миллисекунд");
    }
}