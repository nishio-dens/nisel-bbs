#指定したノードからログを出力する
#例えば，hosts.txtの中身が
# http://localhost:40001/
# http://localhost:40002/
#
#とした場合，
#cat hosts.txt | ruby getstatus.rb 
#とすることで，指定したホストの状態が取得できる

while line = gets
    #末尾の改行削除
    line = line.chomp
    filename = line.gsub(":","")
	filename = filename.gsub(".","")
	filename = filename.gsub("/","")
	filename = "./data/" + filename
	command = 'wget ' + line + 'command/status -o log.log -O' + filename + '_status.txt'
    #wget コマンド実行
    system( command )
    #ノード名

    #infile = open("status.txt", "r")
    #puts "NODE:" + line.chomp
    #while l = infile.gets
        #ログ表示
    #    print l
    #end
    #puts "NODEEND"
    #infile.close
    #status.txtを削除
    #system( 'rm status.txt' )
end
